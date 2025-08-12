package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.RandomVideoCallRequestDto;
import com.programming.techie.springredditclone.dto.RandomVideoCallResponseDto;
import com.programming.techie.springredditclone.dto.VideoCallSessionDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.RandomVideoCallQueue;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.RandomVideoCallQueueRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.RandomVideoCallService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.annotation.PostConstruct;

@Service
@Slf4j
@Transactional
public class RandomVideoCallServiceImpl implements RandomVideoCallService {

    private final RandomVideoCallQueueRepository queueRepository;
    private final UserRepository userRepository;
    private final VideoCallServiceImpl videoCallService;
    
    // In-memory queue for faster matching
    private final ConcurrentHashMap<String, RandomVideoCallQueue> activeQueue = new ConcurrentHashMap<>();
    private final AtomicLong queueCounter = new AtomicLong(0);
    
    // Matching control
    private volatile boolean matchingEnabled = true;
    private volatile Long lastMatchTime = 0L;
    private volatile Long matchesToday = 0L;
    
    public RandomVideoCallServiceImpl(RandomVideoCallQueueRepository queueRepository, 
                                   UserRepository userRepository, 
                                   VideoCallServiceImpl videoCallService) {
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
        this.videoCallService = videoCallService;
    }

    @Override
    public RandomVideoCallResponseDto requestRandomVideoCall(RandomVideoCallRequestDto request) {
        User currentUser = getCurrentUser();
        
        // Check if user already has an active request
        Optional<RandomVideoCallQueue> existingRequest = queueRepository.findActiveRequestByUser(currentUser);
        if (existingRequest.isPresent()) {
            throw new SpringRedditException("You already have an active random video call request");
        }
        
        // Check if user is blocked from random calls
        if (isUserBlockedFromRandomCalls(currentUser.getUserId())) {
            throw new SpringRedditException("You are blocked from making random video calls");
        }
        
        // Create queue entry
        RandomVideoCallQueue queueEntry = new RandomVideoCallQueue();
        queueEntry.setRequestId(generateRequestId());
        queueEntry.setUser(currentUser);
        queueEntry.setQueueStatus("waiting");
        queueEntry.setRequestCreatedAt(Instant.now());
        queueEntry.setLastActivityAt(Instant.now());
        
        // Set call settings
        queueEntry.setCallType(request.getCallType());
        queueEntry.setCallPurpose(request.getCallPurpose());
        queueEntry.setVideoEnabled(request.isEnableVideo());
        queueEntry.setAudioEnabled(request.isEnableAudio());
        queueEntry.setVideoQuality(request.getVideoQuality());
        queueEntry.setAudioQuality(request.getAudioQuality());
        queueEntry.setNetworkType(request.getNetworkType());
        queueEntry.setDeviceType(request.getDeviceType());
        queueEntry.setLocation(request.getLocation());
        queueEntry.setTimezone(request.getTimezone());
        
        // Set matching preferences
        queueEntry.setPreferredGender(request.getPreferredGender());
        queueEntry.setPreferredAgeRange(request.getPreferredAgeRange());
        queueEntry.setPreferredLanguage(request.getPreferredLanguage());
        queueEntry.setPreferredLocation(request.getPreferredLocation());
        queueEntry.setPreferredInterests(request.getPreferredInterests());
        
        // Set queue settings
        queueEntry.setIsPriority(request.isPriority());
        queueEntry.setQueueType(request.getQueueType());
        queueEntry.setMaxWaitTime(request.getMaxWaitTime());
        
        // Calculate queue position
        Long totalInQueue = queueRepository.countWaitingUsers();
        queueEntry.setQueuePosition(totalInQueue + 1);
        queueEntry.setEstimatedWaitTime(calculateEstimatedWaitTime(totalInQueue + 1));
        queueEntry.setTotalUsersInQueue(totalInQueue + 1);
        
        // Save to database
        RandomVideoCallQueue savedEntry = queueRepository.save(queueEntry);
        
        // Add to in-memory queue for faster matching
        activeQueue.put(savedEntry.getRequestId(), savedEntry);
        
        log.info("User {} joined random video call queue with request ID: {}", 
                currentUser.getUsername(), savedEntry.getRequestId());
        
        return mapToResponseDto(savedEntry);
    }

    @Override
    public RandomVideoCallResponseDto checkQueueStatus(String requestId) {
        RandomVideoCallQueue queueEntry = queueRepository.findByRequestId(requestId)
                .orElseThrow(() -> new SpringRedditException("Request not found"));
        
        // Update queue position and wait time
        updateQueuePosition(queueEntry);
        
        return mapToResponseDto(queueEntry);
    }

    @Override
    public void cancelRandomVideoCall(String requestId) {
        RandomVideoCallQueue queueEntry = queueRepository.findByRequestId(requestId)
                .orElseThrow(() -> new SpringRedditException("Request not found"));
        
        User currentUser = getCurrentUser();
        if (!queueEntry.getUser().equals(currentUser)) {
            throw new SpringRedditException("You can only cancel your own requests");
        }
        
        queueEntry.setQueueStatus("cancelled");
        queueEntry.setLastActivityAt(Instant.now());
        queueRepository.save(queueEntry);
        
        // Remove from in-memory queue
        activeQueue.remove(requestId);
        
        log.info("User {} cancelled random video call request: {}", 
                currentUser.getUsername(), requestId);
    }

    @Override
    public RandomVideoCallResponseDto acceptMatchedCall(String requestId) {
        RandomVideoCallQueue queueEntry = queueRepository.findByRequestId(requestId)
                .orElseThrow(() -> new SpringRedditException("Request not found"));
        
        User currentUser = getCurrentUser();
        if (!queueEntry.getUser().equals(currentUser)) {
            throw new SpringRedditException("You can only accept your own matched calls");
        }
        
        if (!"matched".equals(queueEntry.getQueueStatus())) {
            throw new SpringRedditException("Call is not in matched status");
        }
        
        // Update status to connected
        queueEntry.setQueueStatus("connected");
        queueEntry.setCallStartedAt(Instant.now());
        queueEntry.setLastActivityAt(Instant.now());
        
        RandomVideoCallQueue savedEntry = queueRepository.save(queueEntry);
        
        log.info("User {} accepted matched random video call: {}", 
                currentUser.getUsername(), requestId);
        
        return mapToResponseDto(savedEntry);
    }

    @Override
    public void declineMatchedCall(String requestId, String reason) {
        RandomVideoCallQueue queueEntry = queueRepository.findByRequestId(requestId)
                .orElseThrow(() -> new SpringRedditException("Request not found"));
        
        User currentUser = getCurrentUser();
        if (!queueEntry.getUser().equals(currentUser)) {
            throw new SpringRedditException("You can only decline your own matched calls");
        }
        
        queueEntry.setQueueStatus("declined");
        queueEntry.setErrorMessage(reason);
        queueEntry.setLastActivityAt(Instant.now());
        queueRepository.save(queueEntry);
        
        // Remove from in-memory queue
        activeQueue.remove(requestId);
        
        log.info("User {} declined matched random video call: {} - Reason: {}", 
                currentUser.getUsername(), requestId, reason);
    }

    @Override
    public RandomVideoCallResponseDto endRandomVideoCall(String requestId) {
        RandomVideoCallQueue queueEntry = queueRepository.findByRequestId(requestId)
                .orElseThrow(() -> new SpringRedditException("Request not found"));
        
        User currentUser = getCurrentUser();
        if (!queueEntry.getUser().equals(currentUser)) {
            throw new SpringRedditException("You can only end your own calls");
        }
        
        // Calculate call duration
        if (queueEntry.getCallStartedAt() != null) {
            long duration = ChronoUnit.SECONDS.between(queueEntry.getCallStartedAt(), Instant.now());
            queueEntry.setTimeInQueue(duration);
        }
        
        queueEntry.setQueueStatus("ended");
        queueEntry.setLastActivityAt(Instant.now());
        
        RandomVideoCallQueue savedEntry = queueRepository.save(queueEntry);
        
        // Remove from in-memory queue
        activeQueue.remove(requestId);
        
        log.info("User {} ended random video call: {} - Duration: {} seconds", 
                currentUser.getUsername(), requestId, queueEntry.getTimeInQueue());
        
        return mapToResponseDto(savedEntry);
    }

    @Override
    public QueueStatistics getQueueStatistics() {
        QueueStatistics stats = new QueueStatistics();
        
        Long totalInQueue = queueRepository.countWaitingUsers();
        Long priorityInQueue = queueRepository.countWaitingUsersByType("priority");
        
        stats.setTotalUsersInQueue(totalInQueue);
        stats.setPriorityUsersInQueue(priorityInQueue);
        stats.setAverageWaitTime(calculateAverageWaitTime());
        stats.setEstimatedWaitTime(calculateEstimatedWaitTime(totalInQueue));
        
        // Calculate today's statistics
        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        List<RandomVideoCallQueue> todayRequests = queueRepository.findByTimeRange(startOfDay, Instant.now());
        
        long totalMatchesToday = todayRequests.stream()
                .filter(r -> "matched".equals(r.getQueueStatus()))
                .count();
        
        long successfulCallsToday = todayRequests.stream()
                .filter(r -> "connected".equals(r.getQueueStatus()))
                .count();
        
        long failedCallsToday = todayRequests.stream()
                .filter(r -> "timeout".equals(r.getQueueStatus()) || "cancelled".equals(r.getQueueStatus()))
                .count();
        
        stats.setTotalMatchesToday(totalMatchesToday);
        stats.setSuccessfulCallsToday(successfulCallsToday);
        stats.setFailedCallsToday(failedCallsToday);
        
        return stats;
    }

    @Override
    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public int processQueueMatching() {
        // Check if matching is enabled
        if (!matchingEnabled) {
            log.debug("Queue matching is currently DISABLED - skipping processing");
            return 0;
        }
        
        List<RandomVideoCallQueue> waitingUsers = queueRepository.findAllWaitingUsers();
        
        if (waitingUsers.size() < 2) {
            return 0; // Need at least 2 users to match
        }
        
        int matchesMade = 0;
        
        // Process priority users first
        List<RandomVideoCallQueue> priorityUsers = queueRepository.findPriorityWaitingUsers();
        matchesMade += matchUsers(priorityUsers);
        
        // Process regular users
        List<RandomVideoCallQueue> regularUsers = waitingUsers.stream()
                .filter(u -> !u.getIsPriority())
                .toList();
        matchesMade += matchUsers(regularUsers);
        
        // Process timed out requests
        processTimedOutRequests();
        
        // Update match statistics
        if (matchesMade > 0) {
            lastMatchTime = System.currentTimeMillis();
            matchesToday += matchesMade;
        }
        
        log.info("Queue matching processed: {} matches made", matchesMade);
        return matchesMade;
    }

    @Override
    public List<RandomVideoCallResponseDto> getRecentRandomVideoCalls(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SpringRedditException("User not found"));
        
        List<RandomVideoCallQueue> recentRequests = queueRepository.findRecentRequestsByUser(user);
        
        return recentRequests.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public RandomVideoCallResponseDto getCurrentUserActiveRequest() {
        User currentUser = getCurrentUser();
        Optional<RandomVideoCallQueue> activeRequest = queueRepository.findActiveRequestByUser(currentUser);
        
        if (activeRequest.isPresent()) {
            return mapToResponseDto(activeRequest.get());
        }
        
        return null; // No active request found
    }

    @Override
    public RandomVideoCallResponseDto updatePreferences(String requestId, RandomVideoCallRequestDto preferences) {
        RandomVideoCallQueue queueEntry = queueRepository.findByRequestId(requestId)
                .orElseThrow(() -> new SpringRedditException("Request not found"));
        
        User currentUser = getCurrentUser();
        if (!queueEntry.getUser().equals(currentUser)) {
            throw new SpringRedditException("You can only update your own requests");
        }
        
        if (!"waiting".equals(queueEntry.getQueueStatus())) {
            throw new SpringRedditException("Can only update preferences while waiting");
        }
        
        // Update preferences
        queueEntry.setPreferredGender(preferences.getPreferredGender());
        queueEntry.setPreferredAgeRange(preferences.getPreferredAgeRange());
        queueEntry.setPreferredLanguage(preferences.getPreferredLanguage());
        queueEntry.setPreferredLocation(preferences.getPreferredLocation());
        queueEntry.setPreferredInterests(preferences.getPreferredInterests());
        queueEntry.setIsPriority(preferences.isPriority());
        queueEntry.setQueueType(preferences.getQueueType());
        queueEntry.setLastActivityAt(Instant.now());
        
        RandomVideoCallQueue savedEntry = queueRepository.save(queueEntry);
        
        return mapToResponseDto(savedEntry);
    }

    @Override
    public void reportUser(String requestId, String reason) {
        RandomVideoCallQueue queueEntry = queueRepository.findByRequestId(requestId)
                .orElseThrow(() -> new SpringRedditException("Request not found"));
        
        User currentUser = getCurrentUser();
        if (!queueEntry.getUser().equals(currentUser)) {
            throw new SpringRedditException("You can only report from your own requests");
        }
        
        // Log the report
        log.warn("User {} reported user {} from random video call: {} - Reason: {}", 
                currentUser.getUsername(), queueEntry.getMatchedUsername(), requestId, reason);
        
        // TODO: Implement reporting logic (store in database, notify admins, etc.)
    }

    @Override
    public void blockUserFromRandomCalls(Long userId) {
        // TODO: Implement blocking logic
        log.info("User {} blocked from random video calls", userId);
    }

    @Override
    public void unblockUserFromRandomCalls(Long userId) {
        // TODO: Implement unblocking logic
        log.info("User {} unblocked from random video calls", userId);
    }

    @Override
    public List<Long> getBlockedUsersForRandomCalls() {
        // TODO: Implement blocked users retrieval
        return List.of();
    }

    // Helper methods
    private User getCurrentUser() {
        // Extract user ID from JWT token in security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SpringRedditException("User not authenticated");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User not found: " + username));
    }
    
    private String generateRequestId() {
        return "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
    
    private Long calculateEstimatedWaitTime(Long queuePosition) {
        // Simple estimation: 30 seconds per position
        return queuePosition * 30L;
    }
    
    private Long calculateAverageWaitTime() {
        // TODO: Implement average wait time calculation
        return 120L; // Default 2 minutes
    }
    
    private void updateQueuePosition(RandomVideoCallQueue queueEntry) {
        if ("waiting".equals(queueEntry.getQueueStatus())) {
            Long totalInQueue = queueRepository.countWaitingUsers();
            queueEntry.setQueuePosition(totalInQueue);
            queueEntry.setEstimatedWaitTime(calculateEstimatedWaitTime(totalInQueue));
            queueEntry.setTotalUsersInQueue(totalInQueue);
            queueRepository.save(queueEntry);
        }
    }
    
    private int matchUsers(List<RandomVideoCallQueue> users) {
        int matchesMade = 0;
        
        for (int i = 0; i < users.size() - 1; i += 2) {
            RandomVideoCallQueue user1 = users.get(i);
            RandomVideoCallQueue user2 = users.get(i + 1);
            
            if (canMatchUsers(user1, user2)) {
                matchUsers(user1, user2);
                matchesMade++;
            }
        }
        
        return matchesMade;
    }
    
    private boolean canMatchUsers(RandomVideoCallQueue user1, RandomVideoCallQueue user2) {
        // Check if users are still waiting
        if (!"waiting".equals(user1.getQueueStatus()) || !"waiting".equals(user2.getQueueStatus())) {
            return false;
        }
        
        // Check if users are not the same
        if (user1.getUser().equals(user2.getUser())) {
            return false;
        }
        
        // Check if users are not blocked
        if (isUserBlockedFromRandomCalls(user1.getUser().getUserId()) || 
            isUserBlockedFromRandomCalls(user2.getUser().getUserId())) {
            return false;
        }
        
        // Check preferences compatibility
        return arePreferencesCompatible(user1, user2);
    }
    
    private boolean arePreferencesCompatible(RandomVideoCallQueue user1, RandomVideoCallQueue user2) {
        // Check gender preferences
        if (!isGenderCompatible(user1.getPreferredGender(), user2.getPreferredGender())) {
            return false;
        }
        
        // Check age range preferences
        if (!isAgeRangeCompatible(user1.getPreferredAgeRange(), user2.getPreferredAgeRange())) {
            return false;
        }
        
        // Check language preferences
        if (!isLanguageCompatible(user1.getPreferredLanguage(), user2.getPreferredLanguage())) {
            return false;
        }
        
        return true;
    }
    
    private boolean isGenderCompatible(String gender1, String gender2) {
        if ("any".equals(gender1) || "any".equals(gender2)) {
            return true;
        }
        return gender1.equals(gender2);
    }
    
    private boolean isAgeRangeCompatible(String ageRange1, String ageRange2) {
        if ("any".equals(ageRange1) || "any".equals(ageRange2)) {
            return true;
        }
        return ageRange1.equals(ageRange2);
    }
    
    private boolean isLanguageCompatible(String language1, String language2) {
        if ("any".equals(language1) || "any".equals(language2)) {
            return true;
        }
        return language1.equals(language2);
    }
    
    private void matchUsers(RandomVideoCallQueue user1, RandomVideoCallQueue user2) {
        // Update user1
        user1.setQueueStatus("connected"); // Automatically accept - skip "matched" status
        user1.setMatchedAt(Instant.now());
        user1.setMatchedUserId(user2.getUser().getUserId());
        user1.setMatchedUsername(user2.getUser().getUsername());
        user1.setMatchedDisplayName(user2.getUser().getUsername());
        user1.setMatchedProfilePicture("default-avatar.png"); // Default avatar
        user1.setMatchScore(calculateMatchScore(user1, user2));
        user1.setMatchReason("Random match based on preferences");
        user1.setLastActivityAt(Instant.now());
        user1.setCallStartedAt(Instant.now()); // Set call start time immediately
        
        // Update user2
        user2.setQueueStatus("connected"); // Automatically accept - skip "matched" status
        user2.setMatchedAt(Instant.now());
        user2.setMatchedUserId(user1.getUser().getUserId());
        user2.setMatchedUsername(user1.getUser().getUsername());
        user2.setMatchedDisplayName(user1.getUser().getUsername());
        user2.setMatchedProfilePicture("default-avatar.png"); // Default avatar
        user2.setMatchScore(calculateMatchScore(user2, user1));
        user2.setMatchReason("Random match based on preferences");
        user2.setLastActivityAt(Instant.now());
        user2.setCallStartedAt(Instant.now()); // Set call start time immediately
        
        // Generate WebRTC session info
        String sessionId = "random_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String roomId = "room_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String peerId = "peer_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        
        user1.setSessionId(sessionId);
        user1.setRoomId(roomId);
        user1.setPeerId(peerId);
        
        user2.setSessionId(sessionId);
        user2.setRoomId(roomId);
        user2.setPeerId(peerId);
        
        // Save both users
        queueRepository.save(user1);
        queueRepository.save(user2);
        
        log.info("Automatically connected users {} and {} for random video call", 
                user1.getUser().getUsername(), user2.getUser().getUsername());
    }
    
    private Double calculateMatchScore(RandomVideoCallQueue user1, RandomVideoCallQueue user2) {
        double score = 0.5; // Base score
        
        // Add score for matching preferences
        if (user1.getPreferredGender().equals(user2.getPreferredGender())) {
            score += 0.2;
        }
        if (user1.getPreferredAgeRange().equals(user2.getPreferredAgeRange())) {
            score += 0.2;
        }
        if (user1.getPreferredLanguage().equals(user2.getPreferredLanguage())) {
            score += 0.1;
        }
        
        return Math.min(score, 1.0);
    }
    
    private void processTimedOutRequests() {
        Instant timeoutThreshold = Instant.now().minus(5, ChronoUnit.MINUTES);
        List<RandomVideoCallQueue> timedOutRequests = queueRepository.findTimedOutRequests(timeoutThreshold);
        
        for (RandomVideoCallQueue request : timedOutRequests) {
            request.setQueueStatus("timeout");
            request.setErrorMessage("Request timed out");
            request.setLastActivityAt(Instant.now());
            queueRepository.save(request);
            
            // Remove from in-memory queue
            activeQueue.remove(request.getRequestId());
            
            log.info("Request {} timed out for user {}", 
                    request.getRequestId(), request.getUser().getUsername());
        }
    }
    
    private boolean isUserBlockedFromRandomCalls(Long userId) {
        // TODO: Implement blocked users check
        return false;
    }
    
    private RandomVideoCallResponseDto mapToResponseDto(RandomVideoCallQueue queueEntry) {
        RandomVideoCallResponseDto response = new RandomVideoCallResponseDto();
        
        response.setRequestId(queueEntry.getRequestId());
        response.setQueueStatus(queueEntry.getQueueStatus());
        response.setQueuePosition(queueEntry.getQueuePosition());
        response.setEstimatedWaitTime(queueEntry.getEstimatedWaitTime());
        
        // Match information
        response.setSessionId(queueEntry.getSessionId());
        response.setMatchedUserId(queueEntry.getMatchedUserId());
        response.setMatchedUsername(queueEntry.getMatchedUsername());
        response.setMatchedDisplayName(queueEntry.getMatchedDisplayName());
        response.setMatchedProfilePicture(queueEntry.getMatchedProfilePicture());
        
        // Call settings
        response.setCallType(queueEntry.getCallType());
        response.setCallPurpose(queueEntry.getCallPurpose());
        response.setVideoEnabled(queueEntry.isVideoEnabled());
        response.setAudioEnabled(queueEntry.isAudioEnabled());
        response.setVideoQuality(queueEntry.getVideoQuality());
        response.setAudioQuality(queueEntry.getAudioQuality());
        
        // Connection information
        response.setNetworkType(queueEntry.getNetworkType());
        response.setDeviceType(queueEntry.getDeviceType());
        response.setConnectionQuality(queueEntry.getConnectionQuality());
        response.setBandwidth(queueEntry.getBandwidth());
        response.setLatency(queueEntry.getLatency());
        
        // WebRTC information
        response.setRoomId(queueEntry.getRoomId());
        response.setPeerId(queueEntry.getPeerId());
        response.setSignalingData(queueEntry.getSignalingData());
        response.setOfferSdp(queueEntry.getOfferSdp());
        response.setAnswerSdp(queueEntry.getAnswerSdp());
        response.setIceCandidates(queueEntry.getIceCandidates());
        response.setSignalingServer(queueEntry.getSignalingServer());
        response.setStunServer(queueEntry.getStunServer());
        response.setTurnServer(queueEntry.getTurnServer());
        
        // Match quality
        response.setMatchScore(queueEntry.getMatchScore());
        response.setMatchReason(queueEntry.getMatchReason());
        response.setCommonInterests(queueEntry.getCommonInterests());
        response.setLanguageCompatibility(queueEntry.getLanguageCompatibility());
        
        // Queue statistics
        response.setTotalUsersInQueue(queueEntry.getTotalUsersInQueue());
        response.setAverageWaitTime(queueEntry.getAverageWaitTime());
        response.setQueueType(queueEntry.getQueueType());
        response.setIsPriority(queueEntry.getIsPriority());
        
        // Timing information
        response.setRequestCreatedAt(queueEntry.getRequestCreatedAt());
        response.setMatchedAt(queueEntry.getMatchedAt());
        response.setCallStartedAt(queueEntry.getCallStartedAt());
        response.setTimeInQueue(queueEntry.getTimeInQueue());
        
        // Error information
        response.setErrorCode(queueEntry.getErrorCode());
        response.setErrorMessage(queueEntry.getErrorMessage());
        response.setHasError(queueEntry.isHasError());
        
        // User preferences
        response.setPreferredGender(queueEntry.getPreferredGender());
        response.setPreferredAgeRange(queueEntry.getPreferredAgeRange());
        response.setPreferredLanguage(queueEntry.getPreferredLanguage());
        response.setPreferredLocation(queueEntry.getPreferredLocation());
        response.setPreferredInterests(queueEntry.getPreferredInterests());
        
        // Call controls
        response.setCanMute(true);
        response.setCanToggleVideo(true);
        response.setCanSwitchCamera(true);
        response.setCanScreenShare(queueEntry.isEnableScreenSharing());
        response.setCanRecord(queueEntry.isEnableRecording());
        
        // Privacy and security
        response.setPrivacyLevel(queueEntry.getPrivacyLevel());
        response.setEnableNotifications(queueEntry.isEnableNotifications());
        response.setEncrypted(queueEntry.isEncrypted());
        response.setEncryptionType(queueEntry.getEncryptionType());
        response.setSecureCall(queueEntry.isSecureCall());
        response.setSecurityLevel(queueEntry.getSecurityLevel());
        
        // Call duration limits
        response.setMaxCallDuration(queueEntry.getMaxCallDuration());
        response.setRemainingTime(queueEntry.getRemainingTime());
        response.setTimeLimited(queueEntry.isTimeLimited());
        
        // Additional information
        response.setNotes(queueEntry.getNotes());
        response.setScheduled(queueEntry.isScheduled());
        response.setScheduledTime(queueEntry.getScheduledTime());
        response.setScheduledDuration(queueEntry.getScheduledDuration());
        
        return response;
    }
    
    @Override
    public void enableMatching() {
        matchingEnabled = true;
        log.info("Random video call matching has been ENABLED");
    }
    
    @Override
    public void disableMatching() {
        matchingEnabled = false;
        log.info("Random video call matching has been DISABLED");
    }
    
    @Override
    public boolean isMatchingEnabled() {
        return matchingEnabled;
    }
    
    @Override
    public MatchingSystemStatus getMatchingSystemStatus() {
        MatchingSystemStatus status = new MatchingSystemStatus();
        status.setMatchingEnabled(matchingEnabled);
        status.setTotalUsersInQueue(queueRepository.countWaitingUsers());
        status.setPriorityUsersInQueue(queueRepository.countWaitingUsersByType("priority"));
        status.setAverageWaitTime(calculateAverageWaitTime());
        status.setLastMatchTime(lastMatchTime);
        status.setMatchesToday(matchesToday);
        status.setStatus(matchingEnabled ? "ACTIVE" : "DISABLED");
        status.setMessage(matchingEnabled ? 
            "Matching system is running normally" : 
            "Matching system is currently disabled");
        return status;
    }

    @PostConstruct
    public void initialize() {
        // Update any existing "matched" requests to "connected" status
        updateExistingMatchedRequests();
    }
    
    @Override
    public void updateExistingMatchedRequests() {
        try {
            List<RandomVideoCallQueue> matchedRequests = queueRepository.findByStatus("matched");
            if (!matchedRequests.isEmpty()) {
                log.info("Found {} existing matched requests, updating to connected status", matchedRequests.size());
                
                for (RandomVideoCallQueue request : matchedRequests) {
                    request.setQueueStatus("connected");
                    request.setCallStartedAt(Instant.now());
                    request.setLastActivityAt(Instant.now());
                    queueRepository.save(request);
                    
                    log.info("Updated request {} for user {} from matched to connected", 
                            request.getRequestId(), request.getUser().getUsername());
                }
            }
        } catch (Exception e) {
            log.error("Error updating existing matched requests", e);
        }
    }
} 