package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.dto.MatchRequestDto;
import com.programming.techie.springredditclone.dto.MatchResponseDto;
import com.programming.techie.springredditclone.dto.MatchStatusDto;
import com.programming.techie.springredditclone.dto.MatchCountDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.MatchMapper;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.MatchRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.MatchingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class MatchingServiceImpl implements MatchingService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final BlockService blockService;
    private final MatchMapper matchMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MatchDto> findPotentialMatches(int limit) {
        User currentUser = getCurrentUser();
        
        // Get users who are not already matched with current user
        List<User> potentialUsers = userRepository.findPotentialMatches(currentUser.getUserId(), limit);
        
        // Filter out blocked users
        return potentialUsers.stream()
                .filter(user -> !blockService.hasBlockedUser(user.getUserId()) && 
                               !blockService.isBlockedByUser(user.getUserId()))
                .map(user -> createPotentialMatchDto(currentUser, user))
                .collect(Collectors.toList());
    }

    @Override
    public MatchDto createMatch(Long matchedUserId) {
        User currentUser = getCurrentUser();
        User matchedUser = userRepository.findById(matchedUserId)
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + matchedUserId));

        // Check if user is trying to match with themselves
        if (currentUser.equals(matchedUser)) {
            throw new SpringRedditException("Cannot match with yourself");
        }

        // Check if users are blocked
        if (blockService.hasBlockedUser(matchedUserId) || blockService.isBlockedByUser(matchedUserId)) {
            throw new SpringRedditException("Cannot create match due to block restrictions");
        }

        // Check if match already exists
        Optional<Match> existingMatch = matchRepository.findMatchBetweenUsers(currentUser, matchedUser);
        if (existingMatch.isPresent()) {
            throw new SpringRedditException("Match already exists between these users");
        }

        // Create new match for voice calling
        Match match = new Match();
        match.setUser(currentUser);
        match.setMatchedUser(matchedUser);
        match.setMatchStatus("pending");
        match.setPreferredCommunicationMethod("voice"); // Default to voice calling
        
        Match savedMatch = matchRepository.save(match);
        log.info("Created match between user {} and user {}", currentUser.getUsername(), matchedUser.getUsername());
        
        return matchMapper.mapToDto(savedMatch);
    }

    @Override
    public MatchDto acceptMatch(Long matchId) {
        User currentUser = getCurrentUser();
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new SpringRedditException("Match not found with ID: " + matchId));

        // Verify the current user is the one who received the match
        if (!match.getMatchedUser().equals(currentUser)) {
            throw new SpringRedditException("You can only accept matches sent to you");
        }

        if (!"pending".equals(match.getMatchStatus())) {
            throw new SpringRedditException("Match is not in pending status");
        }

        match.setMatchStatus("accepted");
        match.setLastInteractionAt(Instant.now());
        
        // Check if this creates a mutual match
        Optional<Match> reverseMatch = matchRepository.findMatchBetweenUsers(match.getMatchedUser(), match.getUser());
        if (reverseMatch.isPresent() && "accepted".equals(reverseMatch.get().getMatchStatus())) {
            match.setMutualMatch(true);
            reverseMatch.get().setMutualMatch(true);
            matchRepository.save(reverseMatch.get());
            
            // Enable video calling for mutual matches
            match.setPreferredCommunicationMethod("video");
            reverseMatch.get().setPreferredCommunicationMethod("video");
            matchRepository.save(reverseMatch.get());
        }

        Match savedMatch = matchRepository.save(match);
        log.info("User {} accepted match with user {} - video calling enabled", currentUser.getUsername(), match.getUser().getUsername());
        
        return matchMapper.mapToDto(savedMatch);
    }

    @Override
    public void declineMatch(Long matchId) {
        User currentUser = getCurrentUser();
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new SpringRedditException("Match not found with ID: " + matchId));

        // Verify the current user is the one who received the match
        if (!match.getMatchedUser().equals(currentUser)) {
            throw new SpringRedditException("You can only decline matches sent to you");
        }

        match.setMatchStatus("declined");
        match.setLastInteractionAt(Instant.now());
        
        matchRepository.save(match);
        log.info("User {} declined match with user {}", currentUser.getUsername(), match.getUser().getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDto> getMyMatches() {
        User currentUser = getCurrentUser();
        List<Match> matches = matchRepository.findByUser(currentUser);
        
        return matches.stream()
                .map(matchMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchDto> getMutualMatches() {
        User currentUser = getCurrentUser();
        List<Match> mutualMatches = matchRepository.findMutualMatchesByUser(currentUser);
        
        return mutualMatches.stream()
                .map(matchMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MatchDto getMatchById(Long matchId) {
        User currentUser = getCurrentUser();
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new SpringRedditException("Match not found with ID: " + matchId));

        // Verify the current user is involved in this match
        if (!match.getUser().equals(currentUser) && !match.getMatchedUser().equals(currentUser)) {
            throw new SpringRedditException("You are not authorized to view this match");
        }

        return matchMapper.mapToDto(match);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areUsersMatched(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + userId2));

        Optional<Match> match = matchRepository.findMatchBetweenUsers(user1, user2);
        return match.isPresent() && "accepted".equals(match.get().getMatchStatus());
    }

    @Override
    public MatchStatistics getMatchStatistics() {
        User currentUser = getCurrentUser();
        
        long totalMatches = matchRepository.countByUser(currentUser);
        long mutualMatches = matchRepository.countMutualMatchesByUser(currentUser);
        long pendingMatches = matchRepository.countPendingMatchesByUser(currentUser);
        long acceptedMatches = matchRepository.countByUserAndMatchStatus(currentUser, "accepted");
        long declinedMatches = matchRepository.countByUserAndMatchStatus(currentUser, "declined");
        
        // Calculate average rating (simplified)
        double averageRating = 0.0; // TODO: Implement rating calculation
        
        return new MatchStatistics(totalMatches, mutualMatches, pendingMatches, acceptedMatches, declinedMatches, averageRating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MatchCountDto getMatchCount(MatchCountDto matchCountDto) {
        User currentUser = getCurrentUser();
        
        // Verify the user is requesting their own match count
        if (!currentUser.getUserId().equals(matchCountDto.getUserId())) {
            throw new SpringRedditException("You can only request match counts for yourself");
        }
        
        // Get all matches for the user
        List<Match> userMatches = matchRepository.findByUser(currentUser);
        
        // Apply filters based on the DTO criteria
        List<Match> filteredMatches = userMatches.stream()
                .filter(match -> applyMatchStatusFilter(match, matchCountDto))
                .filter(match -> applyMatchTypeFilter(match, matchCountDto))
                .filter(match -> applyTimeFilters(match, matchCountDto))
                .filter(match -> applyQualityFilters(match, matchCountDto))
                .filter(match -> applyCharacteristicFilters(match, matchCountDto))
                .filter(match -> applyCommunicationFilters(match, matchCountDto))
                .collect(Collectors.toList());
        
        // Calculate various counts
        MatchCountDto result = new MatchCountDto();
        result.setUserId(currentUser.getUserId());
        result.setCountGeneratedAt(Instant.now());
        result.setIsRealTime(true);
        
        // Basic counts
        result.setTotalMatches((long) filteredMatches.size());
        result.setPendingMatches(countByStatus(filteredMatches, "pending"));
        result.setAcceptedMatches(countByStatus(filteredMatches, "accepted"));
        result.setDeclinedMatches(countByStatus(filteredMatches, "declined"));
        result.setMutualMatches(countByCharacteristic(filteredMatches, Match::isMutualMatch, true));
        result.setSuperLikeMatches(countByCharacteristic(filteredMatches, Match::isSuperLike, true));
        result.setVerifiedMatches(countByCharacteristic(filteredMatches, Match::isVerifiedMatch, true));
        result.setUnreadMatches(countByCharacteristic(filteredMatches, Match::isRead, false));
        result.setExpiredMatches(countByCharacteristic(filteredMatches, Match::isExpired, true));
        
        // Communication counts
        result.setMatchesWithMessages(countByCharacteristic(filteredMatches, Match::isHasSentMessage, true) + 
                                   countByCharacteristic(filteredMatches, Match::isHasReceivedMessage, true));
        result.setMatchesWithCalls(countByCharacteristic(filteredMatches, Match::isHasCalled, true) + 
                                 countByCharacteristic(filteredMatches, Match::isHasReceivedCall, true));
        
        // Quality distribution
        result.setHighQualityMatches(countByScoreRange(filteredMatches, 80.0, 100.0));
        result.setMediumQualityMatches(countByScoreRange(filteredMatches, 50.0, 79.9));
        result.setLowQualityMatches(countByScoreRange(filteredMatches, 0.0, 49.9));
        
        // Time-based counts
        Instant sevenDaysAgo = Instant.now().minusSeconds(7 * 24 * 60 * 60);
        Instant thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 60 * 60);
        
        result.setRecentMatches(countByTimeRange(filteredMatches, sevenDaysAgo, Instant.now()));
        result.setRecentlyActiveMatches(countByInteractionTimeRange(filteredMatches, thirtyDaysAgo, Instant.now()));
        
        return result;
    }

    @Override
    public void rateMatch(Long matchId, Integer rating, String feedback) {
        User currentUser = getCurrentUser();
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new SpringRedditException("Match not found with ID: " + matchId));

        // Verify the current user is involved in this match
        if (!match.getUser().equals(currentUser) && !match.getMatchedUser().equals(currentUser)) {
            throw new SpringRedditException("You are not authorized to rate this match");
        }

        if (rating < 1 || rating > 5) {
            throw new SpringRedditException("Rating must be between 1 and 5");
        }

        match.setUserRating(rating);
        match.setUserFeedback(feedback);
        match.setLastInteractionAt(Instant.now());
        
        matchRepository.save(match);
        log.info("User {} rated match {} with rating {}", currentUser.getUsername(), matchId, rating);
    }

    @Override
    public void reportMatch(Long matchId, String reason) {
        User currentUser = getCurrentUser();
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new SpringRedditException("Match not found with ID: " + matchId));

        // Verify the current user is involved in this match
        if (!match.getUser().equals(currentUser) && !match.getMatchedUser().equals(currentUser)) {
            throw new SpringRedditException("You are not authorized to report this match");
        }

        match.setReported(true);
        match.setReportReason(reason);
        match.setLastInteractionAt(Instant.now());
        
        matchRepository.save(match);
        log.info("User {} reported match {} with reason: {}", currentUser.getUsername(), matchId, reason);
    }

    @Override
    public void blockUser(Long userId) {
        User currentUser = getCurrentUser();
        User userToBlock = userRepository.findById(userId)
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + userId));

        if (currentUser.equals(userToBlock)) {
            throw new SpringRedditException("Cannot block yourself");
        }

        // TODO: Implement user blocking logic
        log.info("User {} blocked user {}", currentUser.getUsername(), userToBlock.getUsername());
    }

    @Override
    public void unblockUser(Long userId) {
        User currentUser = getCurrentUser();
        User userToUnblock = userRepository.findById(userId)
                .orElseThrow(() -> new SpringRedditException("User not found with ID: " + userId));

        // TODO: Implement user unblocking logic
        log.info("User {} unblocked user {}", currentUser.getUsername(), userToUnblock.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getBlockedUsers() {
        User currentUser = getCurrentUser();
        // TODO: Implement blocked users retrieval
        return List.of();
    }

    // Helper methods
    protected User getCurrentUser() {
        // TODO: Implement current user retrieval from security context
        throw new SpringRedditException("Current user retrieval not implemented");
    }

    private MatchDto createPotentialMatchDto(User currentUser, User potentialUser) {
        MatchDto dto = new MatchDto();
        dto.setUserId(currentUser.getUserId());
        dto.setMatchedUserId(potentialUser.getUserId());
        dto.setMatchedUserUsername(potentialUser.getUsername());
        dto.setMatchStatus("potential");
        return dto;
    }
    
    // Helper methods for match counting
    private boolean applyMatchStatusFilter(Match match, MatchCountDto dto) {
        if (dto.getMatchStatus() == null || "all".equals(dto.getMatchStatus())) {
            return true;
        }
        return dto.getMatchStatus().equals(match.getMatchStatus());
    }
    
    private boolean applyMatchTypeFilter(Match match, MatchCountDto dto) {
        if (dto.getMatchType() == null || "all".equals(dto.getMatchType())) {
            return true;
        }
        return dto.getMatchType().equals(match.getMatchType());
    }
    
    private boolean applyTimeFilters(Match match, MatchCountDto dto) {
        if (dto.getCreatedAfter() != null && match.getMatchedAt().isBefore(dto.getCreatedAfter())) {
            return false;
        }
        if (dto.getCreatedBefore() != null && match.getMatchedAt().isAfter(dto.getCreatedBefore())) {
            return false;
        }
        if (dto.getLastInteractionAfter() != null && match.getLastInteractionAt() != null && 
            match.getLastInteractionAt().isBefore(dto.getLastInteractionAfter())) {
            return false;
        }
        if (dto.getLastInteractionBefore() != null && match.getLastInteractionAt() != null && 
            match.getLastInteractionAt().isAfter(dto.getLastInteractionBefore())) {
            return false;
        }
        return true;
    }
    
    private boolean applyQualityFilters(Match match, MatchCountDto dto) {
        if (dto.getMinMatchScore() != null && match.getOverallMatchScore() < dto.getMinMatchScore()) {
            return false;
        }
        if (dto.getMaxMatchScore() != null && match.getOverallMatchScore() > dto.getMaxMatchScore()) {
            return false;
        }
        return true;
    }
    
    private boolean applyCharacteristicFilters(Match match, MatchCountDto dto) {
        if (dto.getIsMutualMatch() != null && match.isMutualMatch() != dto.getIsMutualMatch()) {
            return false;
        }
        if (dto.getIsSuperLike() != null && match.isSuperLike() != dto.getIsSuperLike()) {
            return false;
        }
        if (dto.getIsVerifiedMatch() != null && match.isVerifiedMatch() != dto.getIsVerifiedMatch()) {
            return false;
        }
        if (dto.getIsRead() != null && match.isRead() != dto.getIsRead()) {
            return false;
        }
        if (dto.getIsExpired() != null && match.isExpired() != dto.getIsExpired()) {
            return false;
        }
        return true;
    }
    
    private boolean applyCommunicationFilters(Match match, MatchCountDto dto) {
        if (dto.getPreferredCommunicationMethod() != null && !"all".equals(dto.getPreferredCommunicationMethod()) &&
            !dto.getPreferredCommunicationMethod().equals(match.getPreferredCommunicationMethod())) {
            return false;
        }
        if (dto.getHasSentMessage() != null && match.isHasSentMessage() != dto.getHasSentMessage()) {
            return false;
        }
        if (dto.getHasReceivedMessage() != null && match.isHasReceivedMessage() != dto.getHasReceivedMessage()) {
            return false;
        }
        if (dto.getHasCalled() != null && match.isHasCalled() != dto.getHasCalled()) {
            return false;
        }
        if (dto.getHasReceivedCall() != null && match.isHasReceivedCall() != dto.getHasReceivedCall()) {
            return false;
        }
        return true;
    }
    
    private long countByStatus(List<Match> matches, String status) {
        return matches.stream()
                .filter(match -> status.equals(match.getMatchStatus()))
                .count();
    }
    
    private long countByCharacteristic(List<Match> matches, java.util.function.Function<Match, Boolean> characteristic, boolean expectedValue) {
        return matches.stream()
                .filter(match -> characteristic.apply(match) == expectedValue)
                .count();
    }
    
    private long countByScoreRange(List<Match> matches, double minScore, double maxScore) {
        return matches.stream()
                .filter(match -> match.getOverallMatchScore() >= minScore && match.getOverallMatchScore() <= maxScore)
                .count();
    }
    
    private long countByTimeRange(List<Match> matches, Instant startTime, Instant endTime) {
        return matches.stream()
                .filter(match -> match.getMatchedAt().isAfter(startTime) && match.getMatchedAt().isBefore(endTime))
                .count();
    }
    
    private long countByInteractionTimeRange(List<Match> matches, Instant startTime, Instant endTime) {
        return matches.stream()
                .filter(match -> match.getLastInteractionAt() != null && 
                               match.getLastInteractionAt().isAfter(startTime) && 
                               match.getLastInteractionAt().isBefore(endTime))
                .count();
    }
} 