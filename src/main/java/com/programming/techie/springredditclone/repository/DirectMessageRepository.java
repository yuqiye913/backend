package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.DirectMessage;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findBySender(User sender);
    List<DirectMessage> findByReceiver(User receiver);
    List<DirectMessage> findBySenderAndReceiver(User sender, User receiver);
    List<DirectMessage> findByMessageType(String messageType);
    List<DirectMessage> findByIsRead(boolean isRead);
    
    @Query("SELECT dm FROM DirectMessage dm WHERE (dm.sender = :user1 AND dm.receiver = :user2) OR (dm.sender = :user2 AND dm.receiver = :user1) ORDER BY dm.sentAt ASC")
    List<DirectMessage> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("SELECT dm FROM DirectMessage dm WHERE (dm.sender = :user OR dm.receiver = :user) AND dm.sentAt >= :startDate ORDER BY dm.sentAt DESC")
    List<DirectMessage> findMessagesByUserAndDateRange(@Param("user") User user, @Param("startDate") Instant startDate);
    
    @Query("SELECT COUNT(dm) FROM DirectMessage dm WHERE dm.receiver = :user AND dm.isRead = false")
    Long countUnreadMessagesByUser(@Param("user") User user);
    
    @Query("SELECT dm FROM DirectMessage dm WHERE dm.receiver = :user AND dm.isRead = false ORDER BY dm.sentAt DESC")
    List<DirectMessage> findUnreadMessagesByUser(@Param("user") User user);
    
    @Query("SELECT DISTINCT CASE WHEN dm.sender = :user THEN dm.receiver ELSE dm.sender END FROM DirectMessage dm WHERE dm.sender = :user OR dm.receiver = :user")
    List<User> findConversationPartnersByUser(@Param("user") User user);
} 