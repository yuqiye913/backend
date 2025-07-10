package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.model.User;

import java.util.List;

public interface MatchingService {
    
    /**
     * Find potential matches for the current user
     * @param limit Maximum number of matches to return
     * @return List of potential matches
     */
    List<MatchDto> findPotentialMatches(int limit);
    
    /**
     * Create a match between two users
     * @param matchedUserId ID of the user to match with
     * @return Created match
     */
    MatchDto createMatch(Long matchedUserId);
    
    /**
     * Accept a match
     * @param matchId ID of the match to accept
     * @return Updated match
     */
    MatchDto acceptMatch(Long matchId);
    
    /**
     * Decline a match
     * @param matchId ID of the match to decline
     */
    void declineMatch(Long matchId);
    
    /**
     * Get all matches for the current user
     * @return List of matches
     */
    List<MatchDto> getMyMatches();
    
    /**
     * Get mutual matches (where both users accepted)
     * @return List of mutual matches
     */
    List<MatchDto> getMutualMatches();
    
    /**
     * Get a specific match by ID
     * @param matchId ID of the match
     * @return Match details
     */
    MatchDto getMatchById(Long matchId);
    
    /**
     * Check if two users are matched
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if matched, false otherwise
     */
    boolean areUsersMatched(Long userId1, Long userId2);
    
    /**
     * Get match statistics for the current user
     * @return Match statistics
     */
    MatchStatistics getMatchStatistics();
    
    /**
     * Rate a match after interaction
     * @param matchId ID of the match
     * @param rating Rating (1-5)
     * @param feedback Optional feedback
     */
    void rateMatch(Long matchId, Integer rating, String feedback);
    
    /**
     * Report a match
     * @param matchId ID of the match
     * @param reason Reason for reporting
     */
    void reportMatch(Long matchId, String reason);
    
    /**
     * Block a user from future matches
     * @param userId ID of the user to block
     */
    void blockUser(Long userId);
    
    /**
     * Unblock a user
     * @param userId ID of the user to unblock
     */
    void unblockUser(Long userId);
    
    /**
     * Get blocked users
     * @return List of blocked user IDs
     */
    List<Long> getBlockedUsers();
    
    /**
     * Match statistics class
     */
    class MatchStatistics {
        private final long totalMatches;
        private final long mutualMatches;
        private final long pendingMatches;
        private final long acceptedMatches;
        private final long declinedMatches;
        private final double averageRating;
        
        public MatchStatistics(long totalMatches, long mutualMatches, long pendingMatches, 
                             long acceptedMatches, long declinedMatches, double averageRating) {
            this.totalMatches = totalMatches;
            this.mutualMatches = mutualMatches;
            this.pendingMatches = pendingMatches;
            this.acceptedMatches = acceptedMatches;
            this.declinedMatches = declinedMatches;
            this.averageRating = averageRating;
        }
        
        // Getters
        public long getTotalMatches() { return totalMatches; }
        public long getMutualMatches() { return mutualMatches; }
        public long getPendingMatches() { return pendingMatches; }
        public long getAcceptedMatches() { return acceptedMatches; }
        public long getDeclinedMatches() { return declinedMatches; }
        public double getAverageRating() { return averageRating; }
    }
} 