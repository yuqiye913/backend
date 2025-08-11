package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.RandomVideoCallRequestDto;
import com.programming.techie.springredditclone.dto.RandomVideoCallResponseDto;

import java.util.List;

public interface RandomVideoCallService {
    
    /**
     * Request a random video call - user joins the queue
     * @param request Random video call request details
     * @return Response with queue status and position
     */
    RandomVideoCallResponseDto requestRandomVideoCall(RandomVideoCallRequestDto request);
    
    /**
     * Check queue status for a user
     * @param requestId Request ID to check
     * @return Current queue status and position
     */
    RandomVideoCallResponseDto checkQueueStatus(String requestId);
    
    /**
     * Cancel a random video call request
     * @param requestId Request ID to cancel
     */
    void cancelRandomVideoCall(String requestId);
    
    /**
     * Accept a matched video call
     * @param requestId Request ID to accept
     * @return Updated response with connection details
     */
    RandomVideoCallResponseDto acceptMatchedCall(String requestId);
    
    /**
     * Decline a matched video call
     * @param requestId Request ID to decline
     * @param reason Reason for declining
     */
    void declineMatchedCall(String requestId, String reason);
    
    /**
     * End an active random video call
     * @param requestId Request ID to end
     * @return Final call statistics
     */
    RandomVideoCallResponseDto endRandomVideoCall(String requestId);
    
    /**
     * Get queue statistics
     * @return Queue statistics including wait times and user counts
     */
    QueueStatistics getQueueStatistics();
    
    /**
     * Process queue matching (called by scheduler)
     * @return Number of matches made
     */
    int processQueueMatching();
    
    /**
     * Get recent random video calls for a user
     * @param userId User ID
     * @return List of recent random video call requests
     */
    List<RandomVideoCallResponseDto> getRecentRandomVideoCalls(Long userId);
    
    /**
     * Get current user's active random video call request
     * @return Current user's active request, or null if no active request
     */
    RandomVideoCallResponseDto getCurrentUserActiveRequest();
    
    /**
     * Update user preferences for random video calls
     * @param requestId Request ID
     * @param preferences Updated preferences
     * @return Updated response
     */
    RandomVideoCallResponseDto updatePreferences(String requestId, RandomVideoCallRequestDto preferences);
    
    /**
     * Report a user from random video call
     * @param requestId Request ID
     * @param reason Reason for reporting
     */
    void reportUser(String requestId, String reason);
    
    /**
     * Block a user from future random video calls
     * @param userId User ID to block
     */
    void blockUserFromRandomCalls(Long userId);
    
    /**
     * Unblock a user from random video calls
     * @param userId User ID to unblock
     */
    void unblockUserFromRandomCalls(Long userId);
    
    /**
     * Get blocked users for random video calls
     * @return List of blocked user IDs
     */
    List<Long> getBlockedUsersForRandomCalls();
    
    /**
     * Enable the matching process
     */
    void enableMatching();
    
    /**
     * Disable the matching process
     */
    void disableMatching();
    
    /**
     * Check if matching is currently enabled
     * @return true if matching is enabled, false otherwise
     */
    boolean isMatchingEnabled();
    
    /**
     * Get matching system status
     * @return Matching system status information
     */
    MatchingSystemStatus getMatchingSystemStatus();
    
    /**
     * Queue statistics class
     */
    class QueueStatistics {
        private Long totalUsersInQueue;
        private Long priorityUsersInQueue;
        private Long averageWaitTime;
        private Long estimatedWaitTime;
        private Long totalMatchesToday;
        private Long successfulCallsToday;
        private Long failedCallsToday;
        
        // Getters and setters
        public Long getTotalUsersInQueue() { return totalUsersInQueue; }
        public void setTotalUsersInQueue(Long totalUsersInQueue) { this.totalUsersInQueue = totalUsersInQueue; }
        
        public Long getPriorityUsersInQueue() { return priorityUsersInQueue; }
        public void setPriorityUsersInQueue(Long priorityUsersInQueue) { this.priorityUsersInQueue = priorityUsersInQueue; }
        
        public Long getAverageWaitTime() { return averageWaitTime; }
        public void setAverageWaitTime(Long averageWaitTime) { this.averageWaitTime = averageWaitTime; }
        
        public Long getEstimatedWaitTime() { return estimatedWaitTime; }
        public void setEstimatedWaitTime(Long estimatedWaitTime) { this.estimatedWaitTime = estimatedWaitTime; }
        
        public Long getTotalMatchesToday() { return totalMatchesToday; }
        public void setTotalMatchesToday(Long totalMatchesToday) { this.totalMatchesToday = totalMatchesToday; }
        
        public Long getSuccessfulCallsToday() { return successfulCallsToday; }
        public void setSuccessfulCallsToday(Long successfulCallsToday) { this.successfulCallsToday = successfulCallsToday; }
        
        public Long getFailedCallsToday() { return failedCallsToday; }
        public void setFailedCallsToday(Long failedCallsToday) { this.failedCallsToday = failedCallsToday; }
    }
    
    /**
     * Matching system status class
     */
    class MatchingSystemStatus {
        private boolean matchingEnabled;
        private Long totalUsersInQueue;
        private Long priorityUsersInQueue;
        private Long averageWaitTime;
        private Long lastMatchTime;
        private Long matchesToday;
        private String status;
        private String message;
        
        // Getters and setters
        public boolean isMatchingEnabled() { return matchingEnabled; }
        public void setMatchingEnabled(boolean matchingEnabled) { this.matchingEnabled = matchingEnabled; }
        
        public Long getTotalUsersInQueue() { return totalUsersInQueue; }
        public void setTotalUsersInQueue(Long totalUsersInQueue) { this.totalUsersInQueue = totalUsersInQueue; }
        
        public Long getPriorityUsersInQueue() { return priorityUsersInQueue; }
        public void setPriorityUsersInQueue(Long priorityUsersInQueue) { this.priorityUsersInQueue = priorityUsersInQueue; }
        
        public Long getAverageWaitTime() { return averageWaitTime; }
        public void setAverageWaitTime(Long averageWaitTime) { this.averageWaitTime = averageWaitTime; }
        
        public Long getLastMatchTime() { return lastMatchTime; }
        public void setLastMatchTime(Long lastMatchTime) { this.lastMatchTime = lastMatchTime; }
        
        public Long getMatchesToday() { return matchesToday; }
        public void setMatchesToday(Long matchesToday) { this.matchesToday = matchesToday; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 