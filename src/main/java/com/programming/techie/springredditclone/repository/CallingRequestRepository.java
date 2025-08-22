package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.CallingRequest;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CallingRequestRepository extends JpaRepository<CallingRequest, Long> {
    List<CallingRequest> findByCaller(User caller);
    List<CallingRequest> findByReceiver(User receiver);
    List<CallingRequest> findByStatus(String status);
    List<CallingRequest> findByCallType(String callType);
    List<CallingRequest> findByPriority(String priority);
    Optional<CallingRequest> findByCallerAndReceiver(User caller, User receiver);
    
    @Query("SELECT cr FROM CallingRequest cr WHERE cr.receiver = :user AND cr.status = 'pending' ORDER BY cr.requestedAt DESC")
    List<CallingRequest> findPendingRequestsByReceiver(@Param("user") User user);
    
    @Query("SELECT cr FROM CallingRequest cr WHERE cr.caller = :user AND cr.status = 'pending' ORDER BY cr.requestedAt DESC")
    List<CallingRequest> findPendingRequestsByCaller(@Param("user") User user);
    
    @Query("SELECT cr FROM CallingRequest cr WHERE cr.receiver = :user AND cr.isScheduled = true AND cr.scheduledTime >= :now ORDER BY cr.scheduledTime ASC")
    List<CallingRequest> findUpcomingScheduledRequests(@Param("user") User user, @Param("now") Instant now);
    
    @Query("SELECT COUNT(cr) FROM CallingRequest cr WHERE cr.receiver = :user AND cr.status = 'pending'")
    Long countPendingRequestsByReceiver(@Param("user") User user);
    
    @Query("SELECT cr FROM CallingRequest cr WHERE (cr.caller = :user OR cr.receiver = :user) AND cr.status = 'accepted' ORDER BY cr.requestedAt DESC")
    List<CallingRequest> findAcceptedRequestsByUser(@Param("user") User user);
    
    @Query("SELECT cr FROM CallingRequest cr WHERE cr.receiver = :user AND cr.priority = 'urgent' AND cr.status = 'pending' ORDER BY cr.requestedAt DESC")
    List<CallingRequest> findUrgentPendingRequests(@Param("user") User user);
} 