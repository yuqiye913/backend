package com.programming.techie.springredditclone.repository.notification;

import com.programming.techie.springredditclone.model.Notification;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.NotificationRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    @BeforeEach
    void setUp() {
        // Create test users
        user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("user1@test.com");
        user1.setPassword("password");
        user1.setEnabled(true);
        user1.setCreated(Instant.now());
        user1 = entityManager.persistAndFlush(user1);

        user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("user2@test.com");
        user2.setPassword("password");
        user2.setEnabled(true);
        user2.setCreated(Instant.now());
        user2 = entityManager.persistAndFlush(user2);

        user3 = new User();
        user3.setUsername("testuser3");
        user3.setEmail("user3@test.com");
        user3.setPassword("password");
        user3.setEnabled(true);
        user3.setCreated(Instant.now());
        user3 = entityManager.persistAndFlush(user3);

        // Create test notifications
        notification1 = new Notification();
        notification1.setRecipient(user1);
        notification1.setSender(user2);
        notification1.setTitle("Comment Notification");
        notification1.setMessage("User2 commented on your post");
        notification1.setNotificationType("comment");
        notification1.setCategory("social");
        notification1.setPriority("normal");
        notification1.setRead(false);
        notification1.setCreatedAt(Instant.now());
        notification1.setActionUrl("/posts/1");
        notification1.setIcon("comment-icon");
        notification1.setRelatedPostId(1L);
        notification1 = entityManager.persistAndFlush(notification1);

        notification2 = new Notification();
        notification2.setRecipient(user1);
        notification2.setSender(user3);
        notification2.setTitle("Follow Notification");
        notification2.setMessage("User3 started following you");
        notification2.setNotificationType("follow");
        notification2.setCategory("social");
        notification2.setPriority("normal");
        notification2.setRead(true);
        notification2.setReadAt(Instant.now());
        notification2.setCreatedAt(Instant.now().minusSeconds(3600));
        notification2.setActionUrl("/profile/user3");
        notification2.setIcon("follow-icon");
        notification2.setRelatedUserId(3L);
        notification2 = entityManager.persistAndFlush(notification2);

        notification3 = new Notification();
        notification3.setRecipient(user1);
        notification3.setSender(null);
        notification3.setTitle("System Notification");
        notification3.setMessage("System maintenance scheduled");
        notification3.setNotificationType("system");
        notification3.setCategory("system");
        notification3.setPriority("high");
        notification3.setRead(false);
        notification3.setCreatedAt(Instant.now().minusSeconds(7200));
        notification3.setIcon("system-icon");
        notification3 = entityManager.persistAndFlush(notification3);

        entityManager.clear();
    }

    @Test
    void findByRecipientOrderByCreatedAtDesc_ShouldReturnNotificationsForUser() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findByRecipientOrderByCreatedAtDesc(user1, pageable);

        // Assert
        assertEquals(3, result.getContent().size());
        assertTrue(result.getContent().get(0).getCreatedAt().isAfter(result.getContent().get(1).getCreatedAt()));
        assertTrue(result.getContent().get(1).getCreatedAt().isAfter(result.getContent().get(2).getCreatedAt()));
    }

    @Test
    void findByRecipientAndIsReadFalseOrderByCreatedAtDesc_ShouldReturnUnreadNotifications() {
        // Act
        List<Notification> result = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user1);

        // Assert
        assertEquals(2, result.size());
        assertFalse(result.get(0).isRead());
        assertFalse(result.get(1).isRead());
    }

    @Test
    void countByRecipientAndIsReadFalse_ShouldReturnCorrectCount() {
        // Act
        Long result = notificationRepository.countByRecipientAndIsReadFalse(user1);

        // Assert
        assertEquals(2L, result);
    }

    @Test
    void findByRecipientAndNotificationTypeOrderByCreatedAtDesc_ShouldReturnNotificationsByType() {
        // Act
        List<Notification> result = notificationRepository.findByRecipientAndNotificationTypeOrderByCreatedAtDesc(user1, "comment");

        // Assert
        assertEquals(1, result.size());
        assertEquals("comment", result.get(0).getNotificationType());
    }

    @Test
    void findByRecipientAndCategoryOrderByCreatedAtDesc_ShouldReturnNotificationsByCategory() {
        // Act
        List<Notification> result = notificationRepository.findByRecipientAndCategoryOrderByCreatedAtDesc(user1, "social");

        // Assert
        assertEquals(2, result.size());
        assertEquals("social", result.get(0).getCategory());
        assertEquals("social", result.get(1).getCategory());
    }

    @Test
    void findByRecipientAndPriorityOrderByCreatedAtDesc_ShouldReturnNotificationsByPriority() {
        // Act
        List<Notification> result = notificationRepository.findByRecipientAndPriorityOrderByCreatedAtDesc(user1, "high");

        // Assert
        assertEquals(1, result.size());
        assertEquals("high", result.get(0).getPriority());
    }

    @Test
    void findByRecipientAndCreatedAtAfter_ShouldReturnRecentNotifications() {
        // Arrange
        Instant thirtyMinutesAgo = Instant.now().minusSeconds(1800);

        // Act
        List<Notification> result = notificationRepository.findByRecipientAndCreatedAtAfter(user1, thirtyMinutesAgo);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCreatedAt().isAfter(thirtyMinutesAgo));
    }

    @Test
    void findByRecipientAndRelatedPostIdOrderByCreatedAtDesc_ShouldReturnNotificationsByPost() {
        // Act
        List<Notification> result = notificationRepository.findByRecipientAndRelatedPostIdOrderByCreatedAtDesc(user1, 1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRelatedPostId());
    }

    @Test
    void findByRecipientAndRelatedUserIdOrderByCreatedAtDesc_ShouldReturnNotificationsByUser() {
        // Act
        List<Notification> result = notificationRepository.findByRecipientAndRelatedUserIdOrderByCreatedAtDesc(user1, 3L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getRelatedUserId());
    }

    @Test
    void findByRecipientAndSenderOrderByCreatedAtDesc_ShouldReturnNotificationsBySender() {
        // Act
        List<Notification> result = notificationRepository.findByRecipientAndSenderOrderByCreatedAtDesc(user1, user2);

        // Assert
        assertEquals(1, result.size());
        assertEquals(user2, result.get(0).getSender());
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        // Arrange
        Instant readAt = Instant.now();

        // Act
        notificationRepository.markAsRead(notification1.getId(), readAt);

        // Assert
        entityManager.clear();
        Notification updatedNotification = entityManager.find(Notification.class, notification1.getId());
        assertTrue(updatedNotification.isRead());
        assertEquals(readAt, updatedNotification.getReadAt());
    }

    @Test
    void markAllAsRead_ShouldMarkAllNotificationsAsRead() {
        // Arrange
        Instant readAt = Instant.now();

        // Act
        notificationRepository.markAllAsRead(user1, readAt);

        // Assert
        entityManager.clear();
        List<Notification> updatedNotifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user1, PageRequest.of(0, 10)).getContent();
        assertTrue(updatedNotifications.stream().allMatch(Notification::isRead));
    }

    @Test
    void deleteAllForUser_ShouldSoftDeleteAllNotifications() {
        // Arrange
        Instant deletedAt = Instant.now();

        // Act
        notificationRepository.deleteAllForUser(user1, deletedAt);

        // Assert
        entityManager.clear();
        List<Notification> deletedNotifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user1, PageRequest.of(0, 10)).getContent();
        assertTrue(deletedNotifications.stream().allMatch(Notification::isDeleted));
    }

    @Test
    void findRecentNotifications_ShouldReturnRecentNotifications() {
        // Arrange
        Instant thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 60 * 60);

        // Act
        List<Notification> result = notificationRepository.findRecentNotifications(user1, thirtyDaysAgo);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(n -> n.getCreatedAt().isAfter(thirtyDaysAgo)));
    }

    @Test
    void findNotificationsWithFilters_ShouldReturnFilteredNotifications() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findNotificationsWithFilters(
                user1, "comment", null, false, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("comment", result.getContent().get(0).getNotificationType());
        assertFalse(result.getContent().get(0).isRead());
    }

    @Test
    void findNotificationsWithFilters_ShouldReturnAllNotifications_WhenNoFilters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findNotificationsWithFilters(
                user1, null, null, null, pageable);

        // Assert
        assertEquals(3, result.getContent().size());
    }

    @Test
    void findByRecipientAndRelatedCommentIdOrderByCreatedAtDesc_ShouldReturnNotificationsByComment() {
        // Arrange
        notification1.setRelatedCommentId(123L);
        entityManager.persistAndFlush(notification1);

        // Act
        List<Notification> result = notificationRepository.findByRecipientAndRelatedCommentIdOrderByCreatedAtDesc(user1, 123L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getRelatedCommentId());
    }
} 