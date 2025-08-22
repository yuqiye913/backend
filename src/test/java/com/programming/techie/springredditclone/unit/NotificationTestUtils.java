package com.programming.techie.springredditclone.unit;

import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.model.Notification;
import com.programming.techie.springredditclone.model.User;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for creating test data for notification-related tests
 */
public class NotificationTestUtils {

    /**
     * Create a test user with basic information
     */
    public static User createTestUser(Long userId, String username, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setEnabled(true);
        user.setCreated(Instant.now());
        return user;
    }

    /**
     * Create a test notification with basic information
     */
    public static Notification createTestNotification(Long id, User recipient, User sender, String type, String title, String message) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setCategory(getCategoryForType(type));
        notification.setPriority(getPriorityForType(type));
        notification.setRead(false);
        notification.setReadAt(null);
        notification.setCreatedAt(Instant.now());
        notification.setActionUrl(getActionUrlForType(type, id));
        notification.setIcon(getIconForType(type));
        notification.setDeleted(false);
        notification.setDeletedAt(null);
        return notification;
    }

    /**
     * Create a test NotificationDto with basic information
     */
    public static NotificationDto createTestNotificationDto(Long id, String senderUsername, String recipientUsername, 
                                                           String type, String title, String message) {
        return NotificationDto.builder()
                .id(id)
                .senderUsername(senderUsername)
                .recipientUsername(recipientUsername)
                .title(title)
                .message(message)
                .notificationType(type)
                .category(getCategoryForType(type))
                .priority(getPriorityForType(type))
                .isRead(false)
                .readAt(null)
                .createdAt(Instant.now())
                .actionUrl(getActionUrlForType(type, id))
                .icon(getIconForType(type))
                .build();
    }

    /**
     * Create a comment notification
     */
    public static Notification createCommentNotification(User recipient, User sender, Long postId, Long commentId) {
        Notification notification = createTestNotification(
                1L, recipient, sender, "comment", 
                "New Comment", sender.getUsername() + " commented on your post"
        );
        notification.setRelatedPostId(postId);
        notification.setRelatedCommentId(commentId);
        notification.setRelatedUserId(sender.getUserId());
        return notification;
    }

    /**
     * Create a follow notification
     */
    public static Notification createFollowNotification(User recipient, User sender) {
        Notification notification = createTestNotification(
                1L, recipient, sender, "follow",
                "New Follower", sender.getUsername() + " started following you"
        );
        notification.setRelatedUserId(sender.getUserId());
        return notification;
    }

    /**
     * Create a like notification
     */
    public static Notification createLikeNotification(User recipient, User sender, Long postId) {
        Notification notification = createTestNotification(
                1L, recipient, sender, "like",
                "New Like", sender.getUsername() + " liked your post"
        );
        notification.setRelatedPostId(postId);
        notification.setRelatedUserId(sender.getUserId());
        return notification;
    }

    /**
     * Create a match notification
     */
    public static Notification createMatchNotification(User recipient, User sender, Long matchId) {
        Notification notification = createTestNotification(
                1L, recipient, sender, "match",
                "New Match!", "You matched with " + sender.getUsername()
        );
        notification.setRelatedMatchId(matchId);
        notification.setRelatedUserId(sender.getUserId());
        notification.setPriority("high");
        notification.setCategory("matching");
        return notification;
    }

    /**
     * Create a call request notification
     */
    public static Notification createCallRequestNotification(User recipient, User sender, Long callId) {
        Notification notification = createTestNotification(
                1L, recipient, sender, "call_request",
                "Call Request", sender.getUsername() + " wants to call you"
        );
        notification.setRelatedCallId(callId);
        notification.setRelatedUserId(sender.getUserId());
        notification.setPriority("high");
        notification.setCategory("calls");
        return notification;
    }

    /**
     * Create a system notification
     */
    public static Notification createSystemNotification(User recipient, String title, String message, String category) {
        Notification notification = createTestNotification(
                1L, recipient, null, "system", title, message
        );
        notification.setCategory(category);
        notification.setIcon("system-icon");
        return notification;
    }

    /**
     * Create a list of test users
     */
    public static List<User> createTestUsers() {
        return Arrays.asList(
            createTestUser(1L, "testuser1", "user1@test.com"),
            createTestUser(2L, "testuser2", "user2@test.com"),
            createTestUser(3L, "testuser3", "user3@test.com"),
            createTestUser(4L, "testuser4", "user4@test.com")
        );
    }

    /**
     * Create a list of test notifications
     */
    public static List<Notification> createTestNotifications(User recipient, User sender1, User sender2) {
        return Arrays.asList(
            createCommentNotification(recipient, sender1, 1L, 1L),
            createFollowNotification(recipient, sender2),
            createLikeNotification(recipient, sender1, 2L),
            createSystemNotification(recipient, "System Update", "System is updated", "system")
        );
    }

    /**
     * Create a list of test NotificationDtos
     */
    public static List<NotificationDto> createTestNotificationDtos() {
        return Arrays.asList(
            createTestNotificationDto(1L, "testuser2", "testuser1", "comment", "New Comment", "User2 commented on your post"),
            createTestNotificationDto(2L, "testuser3", "testuser1", "follow", "New Follower", "User3 started following you"),
            createTestNotificationDto(3L, "testuser2", "testuser1", "like", "New Like", "User2 liked your post"),
            createTestNotificationDto(4L, null, "testuser1", "system", "System Update", "System is updated")
        );
    }

    /**
     * Update notification as read
     */
    public static Notification updateNotificationAsRead(Notification notification) {
        notification.setRead(true);
        notification.setReadAt(Instant.now());
        return notification;
    }

    /**
     * Update notification as deleted
     */
    public static Notification updateNotificationAsDeleted(Notification notification) {
        notification.setDeleted(true);
        notification.setDeletedAt(Instant.now());
        return notification;
    }

    /**
     * Get category for notification type
     */
    private static String getCategoryForType(String type) {
        switch (type) {
            case "comment":
            case "follow":
            case "like":
                return "social";
            case "match":
                return "matching";
            case "call_request":
                return "calls";
            case "system":
                return "system";
            default:
                return "general";
        }
    }

    /**
     * Get priority for notification type
     */
    private static String getPriorityForType(String type) {
        switch (type) {
            case "match":
            case "call_request":
                return "high";
            case "like":
                return "low";
            default:
                return "normal";
        }
    }

    /**
     * Get action URL for notification type
     */
    private static String getActionUrlForType(String type, Long id) {
        switch (type) {
            case "comment":
                return "/posts/" + id + "#comment-" + id;
            case "follow":
                return "/profile/user" + id;
            case "like":
                return "/posts/" + id;
            case "match":
                return "/matches/" + id;
            case "call_request":
                return "/calls/" + id;
            default:
                return "/notifications/" + id;
        }
    }

    /**
     * Get icon for notification type
     */
    private static String getIconForType(String type) {
        switch (type) {
            case "comment":
                return "comment-icon";
            case "follow":
                return "follow-icon";
            case "like":
                return "like-icon";
            case "match":
                return "match-icon";
            case "call_request":
                return "call-icon";
            case "system":
                return "system-icon";
            default:
                return "notification-icon";
        }
    }

    /**
     * Create a high priority notification
     */
    public static Notification createHighPriorityNotification(User recipient, User sender, String type, String title, String message) {
        Notification notification = createTestNotification(1L, recipient, sender, type, title, message);
        notification.setPriority("high");
        return notification;
    }

    /**
     * Create a read notification
     */
    public static Notification createReadNotification(User recipient, User sender, String type, String title, String message) {
        Notification notification = createTestNotification(1L, recipient, sender, type, title, message);
        notification.setRead(true);
        notification.setReadAt(Instant.now());
        return notification;
    }

    /**
     * Create a deleted notification
     */
    public static Notification createDeletedNotification(User recipient, User sender, String type, String title, String message) {
        Notification notification = createTestNotification(1L, recipient, sender, type, title, message);
        notification.setDeleted(true);
        notification.setDeletedAt(Instant.now());
        return notification;
    }
} 