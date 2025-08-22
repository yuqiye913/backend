package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    
    /**
     * Create a new notification
     * @param notificationDto Notification data
     * @param recipient User who will receive the notification
     * @param sender User who triggered the notification (can be null for system notifications)
     * @return Created notification
     */
    NotificationDto createNotification(NotificationDto notificationDto, User recipient, User sender);
    
    /**
     * Get all notifications for the current user with pagination
     * @param pageable Pagination parameters
     * @return Page of notifications
     */
    Page<NotificationDto> getNotificationsForCurrentUser(Pageable pageable);
    
    /**
     * Get unread notifications for the current user
     * @return List of unread notifications
     */
    List<NotificationDto> getUnreadNotificationsForCurrentUser();
    
    /**
     * Get notifications by type for the current user
     * @param notificationType Type of notification
     * @return List of notifications
     */
    List<NotificationDto> getNotificationsByType(String notificationType);
    
    /**
     * Get notifications by category for the current user
     * @param category Category of notification
     * @return List of notifications
     */
    List<NotificationDto> getNotificationsByCategory(String category);
    
    /**
     * Mark a specific notification as read
     * @param notificationId ID of the notification
     */
    void markNotificationAsRead(Long notificationId);
    
    /**
     * Mark all notifications as read for the current user
     */
    void markAllNotificationsAsRead();
    
    /**
     * Get count of unread notifications for the current user
     * @return Count of unread notifications
     */
    Long getUnreadNotificationCount();
    
    /**
     * Delete a notification (soft delete)
     * @param notificationId ID of the notification
     */
    void deleteNotification(Long notificationId);
    
    /**
     * Delete all notifications for the current user (soft delete)
     */
    void deleteAllNotifications();
    
    /**
     * Create a comment notification
     * @param commenter User who commented
     * @param postOwner User who owns the post
     * @param postId ID of the post
     * @param commentId ID of the comment
     */
    void createCommentNotification(User commenter, User postOwner, Long postId, Long commentId);
    
    /**
     * Create a follow notification
     * @param follower User who followed
     * @param followed User who was followed
     */
    void createFollowNotification(User follower, User followed);
    
    /**
     * Create a like notification
     * @param liker User who liked
     * @param postOwner User who owns the post
     * @param postId ID of the post
     */
    void createLikeNotification(User liker, User postOwner, Long postId);
    
    /**
     * Create a match notification
     * @param user User who was matched
     * @param matchedUser User who matched with
     * @param matchId ID of the match
     */
    void createMatchNotification(User user, User matchedUser, Long matchId);
    
    /**
     * Create a call request notification
     * @param caller User who initiated the call
     * @param receiver User who received the call request
     * @param callId ID of the call request
     */
    void createCallRequestNotification(User caller, User receiver, Long callId);
    
    /**
     * Create a system notification
     * @param recipient User who will receive the notification
     * @param title Notification title
     * @param message Notification message
     * @param category Notification category
     */
    void createSystemNotification(User recipient, String title, String message, String category);
} 