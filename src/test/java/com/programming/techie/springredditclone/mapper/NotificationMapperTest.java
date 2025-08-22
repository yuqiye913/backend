package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.mapper.NotificationMapper;
import com.programming.techie.springredditclone.model.Notification;
import com.programming.techie.springredditclone.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    private NotificationMapper notificationMapper;
    private User recipient;
    private User sender;
    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        notificationMapper = new NotificationMapper();
        
        // Setup test users
        recipient = new User();
        recipient.setUserId(1L);
        recipient.setUsername("testuser1");
        recipient.setEmail("user1@test.com");
        
        sender = new User();
        sender.setUserId(2L);
        sender.setUsername("testuser2");
        sender.setEmail("user2@test.com");
        
        // Setup test notification
        notification = new Notification();
        notification.setId(1L);
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test notification");
        notification.setNotificationType("comment");
        notification.setCategory("social");
        notification.setPriority("normal");
        notification.setRead(false);
        notification.setReadAt(null);
        notification.setCreatedAt(Instant.now());
        notification.setActionUrl("/test");
        notification.setIcon("test-icon");
        notification.setRelatedPostId(1L);
        notification.setRelatedCommentId(1L);
        notification.setRelatedUserId(2L);
        notification.setRelatedMatchId(1L);
        notification.setRelatedCallId(1L);
        
        // Setup test DTO
        notificationDto = NotificationDto.builder()
                .id(1L)
                .senderUsername("testuser2")
                .recipientUsername("testuser1")
                .title("Test Notification")
                .message("This is a test notification")
                .notificationType("comment")
                .category("social")
                .priority("normal")
                .isRead(false)
                .readAt(null)
                .createdAt(Instant.now())
                .actionUrl("/test")
                .icon("test-icon")
                .relatedPostId(1L)
                .relatedCommentId(1L)
                .relatedUserId(2L)
                .relatedMatchId(1L)
                .relatedCallId(1L)
                .build();
    }

    @Test
    void mapToDto_ShouldMapAllFieldsCorrectly() {
        // Act
        NotificationDto result = notificationMapper.mapToDto(notification);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser2", result.getSenderUsername());
        assertEquals("testuser1", result.getRecipientUsername());
        assertEquals("Test Notification", result.getTitle());
        assertEquals("This is a test notification", result.getMessage());
        assertEquals("comment", result.getNotificationType());
        assertEquals("social", result.getCategory());
        assertEquals("normal", result.getPriority());
        assertFalse(result.isRead());
        assertNull(result.getReadAt());
        assertNotNull(result.getCreatedAt());
        assertEquals("/test", result.getActionUrl());
        assertEquals("test-icon", result.getIcon());
        assertEquals(1L, result.getRelatedPostId());
        assertEquals(1L, result.getRelatedCommentId());
        assertEquals(2L, result.getRelatedUserId());
        assertEquals(1L, result.getRelatedMatchId());
        assertEquals(1L, result.getRelatedCallId());
    }

    @Test
    void mapToDto_ShouldHandleNullSender() {
        // Arrange
        notification.setSender(null);

        // Act
        NotificationDto result = notificationMapper.mapToDto(notification);

        // Assert
        assertNotNull(result);
        assertNull(result.getSenderUsername());
    }

    @Test
    void mapToDto_ShouldHandleReadNotification() {
        // Arrange
        Instant readAt = Instant.now();
        notification.setRead(true);
        notification.setReadAt(readAt);

        // Act
        NotificationDto result = notificationMapper.mapToDto(notification);

        // Assert
        assertNotNull(result);
        assertTrue(result.isRead());
        assertEquals(readAt, result.getReadAt());
    }

    @Test
    void mapToDto_ShouldHandleNullRelatedFields() {
        // Arrange
        notification.setRelatedPostId(null);
        notification.setRelatedCommentId(null);
        notification.setRelatedUserId(null);
        notification.setRelatedMatchId(null);
        notification.setRelatedCallId(null);

        // Act
        NotificationDto result = notificationMapper.mapToDto(notification);

        // Assert
        assertNotNull(result);
        assertNull(result.getRelatedPostId());
        assertNull(result.getRelatedCommentId());
        assertNull(result.getRelatedUserId());
        assertNull(result.getRelatedMatchId());
        assertNull(result.getRelatedCallId());
    }

    @Test
    void mapToEntity_ShouldMapAllFieldsCorrectly() {
        // Act
        Notification result = notificationMapper.mapToEntity(notificationDto, recipient, sender);

        // Assert
        assertNotNull(result);
        assertEquals(recipient, result.getRecipient());
        assertEquals(sender, result.getSender());
        assertEquals("Test Notification", result.getTitle());
        assertEquals("This is a test notification", result.getMessage());
        assertEquals("comment", result.getNotificationType());
        assertEquals("social", result.getCategory());
        assertEquals("normal", result.getPriority());
        assertEquals("/test", result.getActionUrl());
        assertEquals("test-icon", result.getIcon());
        assertEquals(1L, result.getRelatedPostId());
        assertEquals(1L, result.getRelatedCommentId());
        assertEquals(2L, result.getRelatedUserId());
        assertEquals(1L, result.getRelatedMatchId());
        assertEquals(1L, result.getRelatedCallId());
    }

    @Test
    void mapToEntity_ShouldHandleNullSender() {
        // Act
        Notification result = notificationMapper.mapToEntity(notificationDto, recipient, null);

        // Assert
        assertNotNull(result);
        assertEquals(recipient, result.getRecipient());
        assertNull(result.getSender());
    }

    @Test
    void mapToEntity_ShouldHandleNullRelatedFields() {
        // Arrange
        notificationDto.setRelatedPostId(null);
        notificationDto.setRelatedCommentId(null);
        notificationDto.setRelatedUserId(null);
        notificationDto.setRelatedMatchId(null);
        notificationDto.setRelatedCallId(null);

        // Act
        Notification result = notificationMapper.mapToEntity(notificationDto, recipient, sender);

        // Assert
        assertNotNull(result);
        assertNull(result.getRelatedPostId());
        assertNull(result.getRelatedCommentId());
        assertNull(result.getRelatedUserId());
        assertNull(result.getRelatedMatchId());
        assertNull(result.getRelatedCallId());
    }

    @Test
    void mapToEntity_ShouldHandleNullOptionalFields() {
        // Arrange
        notificationDto.setActionUrl(null);
        notificationDto.setIcon(null);
        notificationDto.setCategory(null);

        // Act
        Notification result = notificationMapper.mapToEntity(notificationDto, recipient, sender);

        // Assert
        assertNotNull(result);
        assertNull(result.getActionUrl());
        assertNull(result.getIcon());
        assertNull(result.getCategory());
    }

    @Test
    void mapToDto_ShouldHandleSystemNotification() {
        // Arrange
        notification.setSender(null);
        notification.setNotificationType("system");
        notification.setCategory("system");

        // Act
        NotificationDto result = notificationMapper.mapToDto(notification);

        // Assert
        assertNotNull(result);
        assertNull(result.getSenderUsername());
        assertEquals("system", result.getNotificationType());
        assertEquals("system", result.getCategory());
    }

    @Test
    void mapToEntity_ShouldHandleSystemNotification() {
        // Arrange
        notificationDto.setSenderUsername(null);
        notificationDto.setNotificationType("system");
        notificationDto.setCategory("system");

        // Act
        Notification result = notificationMapper.mapToEntity(notificationDto, recipient, null);

        // Assert
        assertNotNull(result);
        assertNull(result.getSender());
        assertEquals("system", result.getNotificationType());
        assertEquals("system", result.getCategory());
    }

    @Test
    void mapToDto_ShouldHandleHighPriorityNotification() {
        // Arrange
        notification.setPriority("high");

        // Act
        NotificationDto result = notificationMapper.mapToDto(notification);

        // Assert
        assertNotNull(result);
        assertEquals("high", result.getPriority());
    }

    @Test
    void mapToEntity_ShouldHandleHighPriorityNotification() {
        // Arrange
        notificationDto.setPriority("high");

        // Act
        Notification result = notificationMapper.mapToEntity(notificationDto, recipient, sender);

        // Assert
        assertNotNull(result);
        assertEquals("high", result.getPriority());
    }

    @Test
    void mapToDto_ShouldHandleMatchNotification() {
        // Arrange
        notification.setNotificationType("match");
        notification.setCategory("matching");
        notification.setRelatedMatchId(123L);

        // Act
        NotificationDto result = notificationMapper.mapToDto(notification);

        // Assert
        assertNotNull(result);
        assertEquals("match", result.getNotificationType());
        assertEquals("matching", result.getCategory());
        assertEquals(123L, result.getRelatedMatchId());
    }

    @Test
    void mapToEntity_ShouldHandleMatchNotification() {
        // Arrange
        notificationDto.setNotificationType("match");
        notificationDto.setCategory("matching");
        notificationDto.setRelatedMatchId(123L);

        // Act
        Notification result = notificationMapper.mapToEntity(notificationDto, recipient, sender);

        // Assert
        assertNotNull(result);
        assertEquals("match", result.getNotificationType());
        assertEquals("matching", result.getCategory());
        assertEquals(123L, result.getRelatedMatchId());
    }
} 