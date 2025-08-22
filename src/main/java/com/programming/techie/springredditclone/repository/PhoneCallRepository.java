package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.PhoneCall;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PhoneCallRepository extends JpaRepository<PhoneCall, Long> {
    List<PhoneCall> findByCaller(User caller);
    List<PhoneCall> findByReceiver(User receiver);
    List<PhoneCall> findByCallerAndReceiver(User caller, User receiver);
    List<PhoneCall> findByCallStatus(String callStatus);
    List<PhoneCall> findByCallType(String callType);
    
    @Query("SELECT pc FROM PhoneCall pc WHERE (pc.caller = :user OR pc.receiver = :user) ORDER BY pc.callStartedAt DESC")
    List<PhoneCall> findCallHistoryByUser(@Param("user") User user);
    
    @Query("SELECT pc FROM PhoneCall pc WHERE (pc.caller = :user OR pc.receiver = :user) AND pc.callStartedAt >= :startDate ORDER BY pc.callStartedAt DESC")
    List<PhoneCall> findCallHistoryByUserAndDateRange(@Param("user") User user, @Param("startDate") Instant startDate);
    
    @Query("SELECT COUNT(pc) FROM PhoneCall pc WHERE (pc.caller = :user OR pc.receiver = :user) AND pc.callStatus = 'answered'")
    Long countAnsweredCallsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(pc) FROM PhoneCall pc WHERE (pc.caller = :user OR pc.receiver = :user) AND pc.callStatus = 'missed'")
    Long countMissedCallsByUser(@Param("user") User user);
} 