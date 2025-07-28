package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.VideoCallRequestDto;
import com.programming.techie.springredditclone.dto.VideoCallResponseDto;
import com.programming.techie.springredditclone.dto.VideoCallStatusDto;
import com.programming.techie.springredditclone.dto.VideoCallSessionDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.VideoCallSession;
import com.programming.techie.springredditclone.repository.MatchRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.repository.VideoCallSessionRepository;
import com.programming.techie.springredditclone.service.MatchingService;
import com.programming.techie.springredditclone.service.VideoCallService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class VideoCallServiceImpl implements VideoCallService {

    private final VideoCallSessionRepository videoCallSessionRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchingService matchingService;

    @Override
    public VideoCallSessionDto initiateVideoCall(VideoCallRequestDto request) {
        User currentUser = getCurrentUser();
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new SpringRedditException("Receiver not found"));
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new SpringRedditException("Match not found"));

        // Verify users can make video calls
        if (!canMakeVideoCall(currentUser.getUserId(), receiver.getUserId())) {
            throw new SpringRedditException("Users are not matched or video calling is not allowed");
        }

        // Check if there's already an active call
        List<VideoCallSession> activeCalls = videoCallSessionRepository.findActiveCallsByUser(currentUser);
        if (!activeCalls.isEmpty()) {
            throw new SpringRedditException("You already have an active call");
        }

        // Create video call session
        VideoCallSession session = new VideoCallSession();
        session.setSessionId(generateSessionId());
        session.setCaller(currentUser);
        session.setReceiver(receiver);
        session.setMatch(match);
        session.setCallType(request.getCallType());
        session.setCallPurpose(request.getCallPurpose());
        session.setVideoEnabled(request.isEnableVideo());
        session.setAudioEnabled(request.isEnableAudio());
        session.setVideoQuality(request.getVideoQuality());
        session.setAudioQuality(request.getAudioQuality());
        session.setNetworkType(request.getNetworkType());
        session.setDeviceType(request.getDeviceType());
        session.setPrivacyLevel(request.getPrivacyLevel());
        session.setRoomId(generateRoomId());
        session.setPeerId(generatePeerId());
        session.setCallStatus("initiated");
        session.setCallStartedAt(Instant.now());
        session.setLastActivityAt(Instant.now());

        VideoCallSession savedSession = videoCallSessionRepository.save(session);
        log.info("Video call initiated: sessionId={}, caller={}, receiver={}", 
                savedSession.getSessionId(), currentUser.getUsername(), receiver.getUsername());

        return mapToSessionDto(savedSession);
    }

    @Override
    public VideoCallSessionDto acceptVideoCall(String sessionId) {
        User currentUser = getCurrentUser();
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        // Verify the current user is the receiver
        if (!session.getReceiver().equals(currentUser)) {
            throw new SpringRedditException("You can only accept calls sent to you");
        }

        if (!"initiated".equals(session.getCallStatus()) && !"ringing".equals(session.getCallStatus())) {
            throw new SpringRedditException("Call is not in a state that can be accepted");
        }

        session.setCallStatus("answered");
        session.setLastActivityAt(Instant.now());
        
        VideoCallSession savedSession = videoCallSessionRepository.save(session);
        log.info("Video call accepted: sessionId={}, receiver={}", 
                savedSession.getSessionId(), currentUser.getUsername());

        return mapToSessionDto(savedSession);
    }

    @Override
    public void declineVideoCall(String sessionId, String reason) {
        User currentUser = getCurrentUser();
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        // Verify the current user is the receiver
        if (!session.getReceiver().equals(currentUser)) {
            throw new SpringRedditException("You can only decline calls sent to you");
        }

        session.setCallStatus("declined");
        session.setCallEndedAt(Instant.now());
        session.setLastActivityAt(Instant.now());
        session.setErrorMessage(reason);
        
        videoCallSessionRepository.save(session);
        log.info("Video call declined: sessionId={}, receiver={}, reason={}", 
                sessionId, currentUser.getUsername(), reason);
    }

    @Override
    public VideoCallSessionDto endVideoCall(String sessionId) {
        User currentUser = getCurrentUser();
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        // Verify the current user is involved in the call
        if (!session.getCaller().equals(currentUser) && !session.getReceiver().equals(currentUser)) {
            throw new SpringRedditException("You are not authorized to end this call");
        }

        session.setCallStatus("ended");
        session.setCallEndedAt(Instant.now());
        session.setLastActivityAt(Instant.now());
        
        // Calculate call duration
        if (session.getCallStartedAt() != null && session.getCallEndedAt() != null) {
            long duration = session.getCallEndedAt().getEpochSecond() - session.getCallStartedAt().getEpochSecond();
            session.setCallDuration(duration);
        }

        VideoCallSession savedSession = videoCallSessionRepository.save(session);
        log.info("Video call ended: sessionId={}, user={}", 
                savedSession.getSessionId(), currentUser.getUsername());

        return mapToSessionDto(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoCallStatusDto getVideoCallStatus(String sessionId) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        return mapToStatusDto(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoCallSessionDto> getActiveVideoCalls(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SpringRedditException("User not found"));

        List<VideoCallSession> activeCalls = videoCallSessionRepository.findActiveCallsByUser(user);
        return activeCalls.stream()
                .map(this::mapToSessionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoCallSessionDto> getVideoCallHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SpringRedditException("User not found"));

        List<VideoCallSession> completedCalls = videoCallSessionRepository.findCompletedCallsByUser(user);
        return completedCalls.stream()
                .map(this::mapToSessionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canMakeVideoCall(Long userId1, Long userId2) {
        // Check if users are matched and accepted
        return matchingService.areUsersMatched(userId1, userId2);
    }

    @Override
    public VideoCallResponseDto generateSignalingData(String sessionId) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        VideoCallResponseDto response = new VideoCallResponseDto();
        response.setSessionId(sessionId);
        response.setCallerId(session.getCaller().getUserId());
        response.setReceiverId(session.getReceiver().getUserId());
        response.setMatchId(session.getMatch().getMatchId());
        response.setCallType(session.getCallType());
        response.setCallStatus(session.getCallStatus());
        response.setRoomId(session.getRoomId());
        response.setPeerId(session.getPeerId());
        response.setSignalingData(session.getSignalingData());
        response.setOfferSdp(session.getOfferSdp());
        response.setAnswerSdp(session.getAnswerSdp());
        response.setIceCandidates(session.getIceCandidates());
        response.setVideoEnabled(session.isVideoEnabled());
        response.setAudioEnabled(session.isAudioEnabled());
        response.setVideoQuality(session.getVideoQuality());
        response.setAudioQuality(session.getAudioQuality());
        response.setNetworkType(session.getNetworkType());
        response.setDeviceType(session.getDeviceType());
        response.setConnectionQuality(session.getConnectionQuality());
        response.setPrivacyLevel(session.getPrivacyLevel());
        response.setEncrypted(session.isEncrypted());
        response.setEncryptionType(session.getEncryptionType());

        return response;
    }

    @Override
    public void updateSignalingData(String sessionId, String signalingData) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        session.setSignalingData(signalingData);
        session.setLastActivityAt(Instant.now());
        
        videoCallSessionRepository.save(session);
        log.info("Signaling data updated for session: {}", sessionId);
    }

    @Override
    public void setAudioMuted(String sessionId, boolean muted) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        User currentUser = getCurrentUser();
        if (session.getCaller().equals(currentUser)) {
            session.setCallerMuted(muted);
        } else if (session.getReceiver().equals(currentUser)) {
            session.setReceiverMuted(muted);
        } else {
            throw new SpringRedditException("You are not authorized to modify this call");
        }

        session.setLastActivityAt(Instant.now());
        videoCallSessionRepository.save(session);
        log.info("Audio muted set to {} for session: {}", muted, sessionId);
    }

    @Override
    public void setVideoEnabled(String sessionId, boolean enabled) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        User currentUser = getCurrentUser();
        if (session.getCaller().equals(currentUser)) {
            session.setCallerVideoEnabled(enabled);
        } else if (session.getReceiver().equals(currentUser)) {
            session.setReceiverVideoEnabled(enabled);
        } else {
            throw new SpringRedditException("You are not authorized to modify this call");
        }

        session.setLastActivityAt(Instant.now());
        videoCallSessionRepository.save(session);
        log.info("Video enabled set to {} for session: {}", enabled, sessionId);
    }

    @Override
    public void switchCamera(String sessionId) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        User currentUser = getCurrentUser();
        String currentCamera = session.getCaller().equals(currentUser) ? 
                session.getCallerCamera() : session.getReceiverCamera();
        
        String newCamera = "front".equals(currentCamera) ? "back" : "front";
        
        if (session.getCaller().equals(currentUser)) {
            session.setCallerCamera(newCamera);
        } else if (session.getReceiver().equals(currentUser)) {
            session.setReceiverCamera(newCamera);
        } else {
            throw new SpringRedditException("You are not authorized to modify this call");
        }

        session.setLastActivityAt(Instant.now());
        videoCallSessionRepository.save(session);
        log.info("Camera switched to {} for session: {}", newCamera, sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoCallStatusDto getCallStatistics(String sessionId) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        return mapToStatusDto(session);
    }

    // Helper methods
    private User getCurrentUser() {
        // This would typically get the current user from security context
        // For now, we'll throw an exception - this should be implemented based on your auth system
        throw new SpringRedditException("Current user not available - implement based on your authentication system");
    }

    private String generateSessionId() {
        return "vc_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateRoomId() {
        return "room_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generatePeerId() {
        return "peer_" + UUID.randomUUID().toString().replace("-", "");
    }

    private VideoCallSessionDto mapToSessionDto(VideoCallSession session) {
        VideoCallSessionDto dto = new VideoCallSessionDto();
        dto.setSessionId(session.getSessionId());
        dto.setCallStatus(session.getCallStatus());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setUpdatedAt(session.getUpdatedAt());
        dto.setCallerId(session.getCaller().getUserId());
        dto.setReceiverId(session.getReceiver().getUserId());
        dto.setMatchId(session.getMatch().getMatchId());
        dto.setCallType(session.getCallType());
        dto.setCallPurpose(session.getCallPurpose());
        dto.setCallStartedAt(session.getCallStartedAt());
        dto.setCallEndedAt(session.getCallEndedAt());
        dto.setCallDuration(session.getCallDuration());
        dto.setLastActivityAt(session.getLastActivityAt());
        dto.setVideoEnabled(session.isVideoEnabled());
        dto.setAudioEnabled(session.isAudioEnabled());
        dto.setScreenSharingEnabled(session.isScreenSharingEnabled());
        dto.setRecordingEnabled(session.isRecordingEnabled());
        dto.setVideoQuality(session.getVideoQuality());
        dto.setAudioQuality(session.getAudioQuality());
        dto.setNetworkType(session.getNetworkType());
        dto.setDeviceType(session.getDeviceType());
        dto.setConnectionQuality(session.getConnectionQuality());
        dto.setBandwidth(session.getBandwidth());
        dto.setLatency(session.getLatency());
        dto.setCallerUsername(session.getCaller().getUsername());
        dto.setCallerDisplayName(session.getCaller().getUsername()); // Using username as display name
        dto.setCallerProfilePicture(null); // No profile picture field in User model
        dto.setReceiverUsername(session.getReceiver().getUsername());
        dto.setReceiverDisplayName(session.getReceiver().getUsername()); // Using username as display name
        dto.setReceiverProfilePicture(null); // No profile picture field in User model
        dto.setMatchType(session.getMatch().getMatchType());
        dto.setMatchScore(session.getMatch().getOverallMatchScore());
        dto.setMatchStatus(session.getMatch().getMatchStatus());
        dto.setCallerMuted(session.isCallerMuted());
        dto.setReceiverMuted(session.isReceiverMuted());
        dto.setCallerVideoEnabled(session.isCallerVideoEnabled());
        dto.setReceiverVideoEnabled(session.isReceiverVideoEnabled());
        dto.setCallerCamera(session.getCallerCamera());
        dto.setReceiverCamera(session.getReceiverCamera());
        dto.setRoomId(session.getRoomId());
        dto.setPeerId(session.getPeerId());
        dto.setSignalingData(session.getSignalingData());
        dto.setOfferSdp(session.getOfferSdp());
        dto.setAnswerSdp(session.getAnswerSdp());
        dto.setIceCandidates(session.getIceCandidates());
        dto.setSignalingServer(session.getSignalingServer());
        dto.setStunServer(session.getStunServer());
        dto.setTurnServer(session.getTurnServer());
        dto.setVideoQualityScore(session.getVideoQualityScore());
        dto.setAudioQualityScore(session.getAudioQualityScore());
        dto.setOverallQualityScore(session.getOverallQualityScore());
        dto.setQualityRecommendation(session.getQualityRecommendation());
        dto.setVideoResolution(session.getVideoResolution());
        dto.setVideoFrameRate(session.getVideoFrameRate());
        dto.setVideoCodec(session.getVideoCodec());
        dto.setAudioCodec(session.getAudioCodec());
        dto.setAudioSampleRate(session.getAudioSampleRate());
        dto.setAudioChannels(session.getAudioChannels());
        dto.setEnableScreenSharing(session.isEnableScreenSharing());
        dto.setEnableRecording(session.isEnableRecording());
        dto.setEnableTranscription(session.isEnableTranscription());
        dto.setEnableBackgroundBlur(session.isEnableBackgroundBlur());
        dto.setEnableNoiseSuppression(session.isEnableNoiseSuppression());
        dto.setEnableEchoCancellation(session.isEnableEchoCancellation());
        dto.setPrivacyLevel(session.getPrivacyLevel());
        dto.setEnableNotifications(session.isEnableNotifications());
        dto.setEncrypted(session.isEncrypted());
        dto.setEncryptionType(session.getEncryptionType());
        dto.setSecureCall(session.isSecureCall());
        dto.setSecurityLevel(session.getSecurityLevel());
        dto.setMaxCallDuration(session.getMaxCallDuration());
        dto.setRemainingTime(session.getRemainingTime());
        dto.setTimeLimited(session.isTimeLimited());
        dto.setErrorCode(session.getErrorCode());
        dto.setErrorMessage(session.getErrorMessage());
        dto.setErrorDetails(session.getErrorDetails());
        dto.setHasError(session.isHasError());
        dto.setAutoAcceptFutureCalls(session.isAutoAcceptFutureCalls());
        dto.setPreferredCallType(session.getPreferredCallType());
        dto.setPreferredCallTime(session.getPreferredCallTime());
        dto.setRequireAdvanceNotice(session.isRequireAdvanceNotice());
        dto.setAdvanceNoticePeriod(session.getAdvanceNoticePeriod());
        dto.setNotes(session.getNotes());
        dto.setMeetingLink(session.getMeetingLink());
        dto.setMeetingPassword(session.getMeetingPassword());
        dto.setScheduled(session.isScheduled());
        dto.setScheduledTime(session.getScheduledTime());
        dto.setScheduledDuration(session.getScheduledDuration());
        
        return dto;
    }

    private VideoCallStatusDto mapToStatusDto(VideoCallSession session) {
        VideoCallStatusDto dto = new VideoCallStatusDto();
        dto.setSessionId(session.getSessionId());
        dto.setCallStatus(session.getCallStatus());
        dto.setStatusUpdatedAt(session.getUpdatedAt());
        dto.setCallerId(session.getCaller().getUserId());
        dto.setReceiverId(session.getReceiver().getUserId());
        dto.setMatchId(session.getMatch().getMatchId());
        dto.setCallType(session.getCallType());
        dto.setCallStartedAt(session.getCallStartedAt());
        dto.setCallEndedAt(session.getCallEndedAt());
        dto.setCallDuration(session.getCallDuration());
        dto.setVideoEnabled(session.isVideoEnabled());
        dto.setAudioEnabled(session.isAudioEnabled());
        dto.setScreenSharingEnabled(session.isScreenSharingEnabled());
        dto.setRecordingEnabled(session.isRecordingEnabled());
        dto.setVideoQuality(session.getVideoQuality());
        dto.setAudioQuality(session.getAudioQuality());
        dto.setConnectionQuality(session.getConnectionQuality());
        dto.setNetworkType(session.getNetworkType());
        dto.setDeviceType(session.getDeviceType());
        dto.setBandwidth(session.getBandwidth());
        dto.setLatency(session.getLatency());
        dto.setVideoResolution(session.getVideoResolution());
        dto.setVideoFrameRate(session.getVideoFrameRate());
        dto.setVideoCodec(session.getVideoCodec());
        dto.setAudioCodec(session.getAudioCodec());
        dto.setAudioSampleRate(session.getAudioSampleRate());
        dto.setAudioChannels(session.getAudioChannels());
        dto.setCallerMuted(session.isCallerMuted());
        dto.setReceiverMuted(session.isReceiverMuted());
        dto.setCallerVideoEnabled(session.isCallerVideoEnabled());
        dto.setReceiverVideoEnabled(session.isReceiverVideoEnabled());
        dto.setCallerCamera(session.getCallerCamera());
        dto.setReceiverCamera(session.getReceiverCamera());
        dto.setRoomId(session.getRoomId());
        dto.setPeerId(session.getPeerId());
        dto.setSignalingServer(session.getSignalingServer());
        dto.setStunServer(session.getStunServer());
        dto.setTurnServer(session.getTurnServer());
        dto.setEncrypted(session.isEncrypted());
        dto.setEncryptionType(session.getEncryptionType());
        dto.setEnableScreenSharing(session.isEnableScreenSharing());
        dto.setEnableRecording(session.isEnableRecording());
        dto.setEnableTranscription(session.isEnableTranscription());
        dto.setEnableBackgroundBlur(session.isEnableBackgroundBlur());
        dto.setEnableNoiseSuppression(session.isEnableNoiseSuppression());
        dto.setEnableEchoCancellation(session.isEnableEchoCancellation());
        dto.setPrivacyLevel(session.getPrivacyLevel());
        dto.setEnableNotifications(session.isEnableNotifications());
        dto.setSecureCall(session.isSecureCall());
        dto.setSecurityLevel(session.getSecurityLevel());
        dto.setMaxCallDuration(session.getMaxCallDuration());
        dto.setRemainingTime(session.getRemainingTime());
        dto.setTimeLimited(session.isTimeLimited());
        dto.setErrorCode(session.getErrorCode());
        dto.setErrorMessage(session.getErrorMessage());
        dto.setErrorDetails(session.getErrorDetails());
        dto.setHasError(session.isHasError());
        dto.setVideoQualityScore(session.getVideoQualityScore());
        dto.setAudioQualityScore(session.getAudioQualityScore());
        dto.setOverallQualityScore(session.getOverallQualityScore());
        dto.setQualityRecommendation(session.getQualityRecommendation());
        
        return dto;
    }
} 