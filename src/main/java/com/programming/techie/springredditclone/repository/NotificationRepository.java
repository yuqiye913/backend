package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Notification;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications for a specific user
    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    // Find unread notifications for a user
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);
    
    // Count unread notifications for a user
    Long countByRecipientAndIsReadFalse(User recipient);
    
    // Find notifications by type
    List<Notification> findByRecipientAndNotificationTypeOrderByCreatedAtDesc(User recipient, String notificationType);
    
    // Find notifications by category
    List<Notification> findByRecipientAndCategoryOrderByCreatedAtDesc(User recipient, String category);
    
    // Find notifications by priority
    List<Notification> findByRecipientAndPriorityOrderByCreatedAtDesc(User recipient, String priority);
    
    // Find notifications within a date range
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.createdAt >= :startDate ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientAndCreatedAtAfter(@Param("recipient") User recipient, @Param("startDate") Instant startDate);
    
    // Find notifications related to specific content
    List<Notification> findByRecipientAndRelatedPostIdOrderByCreatedAtDesc(User recipient, Long postId);
    List<Notification> findByRecipientAndRelatedCommentIdOrderByCreatedAtDesc(User recipient, Long commentId);
    List<Notification> findByRecipientAndRelatedUserIdOrderByCreatedAtDesc(User recipient, Long userId);
    
    // Find notifications from a specific sender
    List<Notification> findByRecipientAndSenderOrderByCreatedAtDesc(User recipient, User sender);
    
    // Mark notifications as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.recipient = :recipient AND n.isRead = false")
    void markAllAsRead(@Param("recipient") User recipient, @Param("readAt") Instant readAt);
    
    // Mark specific notification as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :notificationId")
    void markAsRead(@Param("notificationId") Long notificationId, @Param("readAt") Instant readAt);
    
    // Soft delete notifications
    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.deletedAt = :deletedAt WHERE n.recipient = :recipient AND n.isDeleted = false")
    void deleteAllForUser(@Param("recipient") User recipient, @Param("deletedAt") Instant deletedAt);
    
    // Find recent notifications (last 30 days)
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.createdAt >= :thirtyDaysAgo AND n.isDeleted = false ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("recipient") User recipient, @Param("thirtyDaysAgo") Instant thirtyDaysAgo);
    
    // Find notifications with pagination and filters
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.isDeleted = false " +
           "AND (:notificationType IS NULL OR n.notificationType = :notificationType) " +
           "AND (:category IS NULL OR n.category = :category) " +
           "AND (:isRead IS NULL OR n.isRead = :isRead) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findNotificationsWithFilters(
        @Param("recipient") User recipient,
        @Param("notificationType") String notificationType,
        @Param("category") String category,
        @Param("isRead") Boolean isRead,
        Pageable pageable
    );
} 