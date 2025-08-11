package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.RandomVideoCallQueue;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RandomVideoCallQueueRepository extends JpaRepository<RandomVideoCallQueue, Long> {
    
    /**
     * Find all users waiting in queue
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting' ORDER BY r.requestCreatedAt ASC")
    List<RandomVideoCallQueue> findAllWaitingUsers();
    
    /**
     * Find waiting users by priority
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting' AND r.isPriority = true ORDER BY r.requestCreatedAt ASC")
    List<RandomVideoCallQueue> findPriorityWaitingUsers();
    
    /**
     * Find waiting users by queue type
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting' AND r.queueType = :queueType ORDER BY r.requestCreatedAt ASC")
    List<RandomVideoCallQueue> findWaitingUsersByType(@Param("queueType") String queueType);
    
    /**
     * Find waiting users with matching preferences
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting' " +
           "AND (r.preferredGender = :preferredGender OR r.preferredGender = 'any') " +
           "AND (r.preferredAgeRange = :preferredAgeRange OR r.preferredAgeRange = 'any') " +
           "AND (r.preferredLanguage = :preferredLanguage OR r.preferredLanguage = 'any') " +
           "ORDER BY r.requestCreatedAt ASC")
    List<RandomVideoCallQueue> findWaitingUsersWithPreferences(
            @Param("preferredGender") String preferredGender,
            @Param("preferredAgeRange") String preferredAgeRange,
            @Param("preferredLanguage") String preferredLanguage);
    
    /**
     * Find request by request ID
     */
    Optional<RandomVideoCallQueue> findByRequestId(String requestId);
    
    /**
     * Find request by user
     */
    Optional<RandomVideoCallQueue> findByUser(User user);
    
    /**
     * Find active request by user (not cancelled or timeout)
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.user = :user AND r.queueStatus IN ('waiting', 'matched')")
    Optional<RandomVideoCallQueue> findActiveRequestByUser(@Param("user") User user);
    
    /**
     * Find requests that have timed out
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting' AND r.requestCreatedAt < :timeoutThreshold")
    List<RandomVideoCallQueue> findTimedOutRequests(@Param("timeoutThreshold") Instant timeoutThreshold);
    
    /**
     * Count total users in queue
     */
    @Query("SELECT COUNT(r) FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting'")
    Long countWaitingUsers();
    
    /**
     * Count users by queue type
     */
    @Query("SELECT COUNT(r) FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting' AND r.queueType = :queueType")
    Long countWaitingUsersByType(@Param("queueType") String queueType);
    
    /**
     * Find requests that need to be processed for matching
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = 'waiting' " +
           "AND r.requestCreatedAt >= :minWaitTime " +
           "ORDER BY r.isPriority DESC, r.requestCreatedAt ASC")
    List<RandomVideoCallQueue> findRequestsReadyForMatching(@Param("minWaitTime") Instant minWaitTime);
    
    /**
     * Find requests by status
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = :status ORDER BY r.requestCreatedAt DESC")
    List<RandomVideoCallQueue> findByStatus(@Param("status") String status);
    
    /**
     * Find requests by user and status
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.user = :user AND r.queueStatus = :status")
    List<RandomVideoCallQueue> findByUserAndStatus(@Param("user") User user, @Param("status") String status);
    
    /**
     * Find recent requests by user
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.user = :user ORDER BY r.requestCreatedAt DESC")
    List<RandomVideoCallQueue> findRecentRequestsByUser(@Param("user") User user);
    
    /**
     * Find requests with high match scores
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.queueStatus = 'matched' AND r.matchScore >= :minScore ORDER BY r.matchScore DESC")
    List<RandomVideoCallQueue> findHighQualityMatches(@Param("minScore") Double minScore);
    
    /**
     * Find requests by time range
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.requestCreatedAt BETWEEN :startTime AND :endTime")
    List<RandomVideoCallQueue> findByTimeRange(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    /**
     * Find requests with errors
     */
    @Query("SELECT r FROM RandomVideoCallQueue r WHERE r.hasError = true")
    List<RandomVideoCallQueue> findRequestsWithErrors();
    
    /**
     * Delete old completed requests
     */
    @Query("DELETE FROM RandomVideoCallQueue r WHERE r.queueStatus IN ('connected', 'timeout', 'cancelled') AND r.requestCreatedAt < :cutoffTime")
    void deleteOldRequests(@Param("cutoffTime") Instant cutoffTime);
} 