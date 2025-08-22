package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.CallingResponse;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallingResponseRepository extends JpaRepository<CallingResponse, Long> {
    List<CallingResponse> findByResponder(User responder);
    List<CallingResponse> findByResponseStatus(String responseStatus);
    @Query("SELECT c FROM CallingResponse c WHERE c.request.requestId = :requestId")
    Optional<CallingResponse> findByRequestId(@Param("requestId") Long requestId);
    
    @Query("SELECT cr FROM CallingResponse cr WHERE cr.responder = :user ORDER BY cr.respondedAt DESC")
    List<CallingResponse> findResponsesByResponder(@Param("user") User user);
    
    @Query("SELECT cr FROM CallingResponse cr WHERE cr.responseStatus = 'accepted' AND cr.responder = :user ORDER BY cr.respondedAt DESC")
    List<CallingResponse> findAcceptedResponsesByResponder(@Param("user") User user);
    
    @Query("SELECT cr FROM CallingResponse cr WHERE cr.responseStatus = 'rescheduled' AND cr.responder = :user ORDER BY cr.respondedAt DESC")
    List<CallingResponse> findRescheduledResponsesByResponder(@Param("user") User user);
    
    @Query("SELECT COUNT(cr) FROM CallingResponse cr WHERE cr.responder = :user AND cr.isRead = false")
    Long countUnreadResponsesByResponder(@Param("user") User user);
} 