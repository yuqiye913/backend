package com.programming.techie.springredditclone.service.notification;

import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.NotificationMapper;
import com.programming.techie.springredditclone.model.Notification;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.NotificationRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.NotificationService;
import com.programming.techie.springredditclone.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private AuthService authService;

    @Mock
    private BlockService blockService;

    private NotificationService notificationService;

    private User currentUser;
    private User senderUser;
    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(notificationRepository, notificationMapper, authService, blockService);

        // Setup test users
        currentUser = new User();
        currentUser.setUserId(1L);
        currentUser.setUsername("testuser1");
        currentUser.setEmail("user1@test.com");
        currentUser.setEnabled(true);

        senderUser = new User();
        senderUser.setUserId(2L);
        senderUser.setUsername("testuser2");
        senderUser.setEmail("user2@test.com");
        senderUser.setEnabled(true);

        // Setup test notification
        notification = new Notification();
        notification.setId(1L);
        notification.setRecipient(currentUser);
        notification.setSender(senderUser);
        notification.setTitle("Test Notification");
        notification.setMessage("This is a test notification");
        notification.setNotificationType("comment");
        notification.setCategory("social");
        notification.setPriority("normal");
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        notification.setActionUrl("/test");
        notification.setIcon("test-icon");

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
                .createdAt(Instant.now())
                .actionUrl("/test")
                .icon("test-icon")
                .build();
    }

    @Test
    void createNotification_ShouldCreateNotificationSuccessfully() {
        // Arrange
        when(notificationMapper.mapToEntity(any(NotificationDto.class), eq(currentUser), eq(senderUser)))
                .thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        NotificationDto result = notificationService.createNotification(notificationDto, currentUser, senderUser);

        // Assert
        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());
        assertEquals("comment", result.getNotificationType());
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).mapToDto(notification);
    }

    @Test
    void getNotificationsForCurrentUser_ShouldReturnPaginatedNotifications() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> notifications = Arrays.asList(notification);
        Page<Notification> notificationPage = new PageImpl<>(notifications, pageable, 1);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(currentUser, pageable))
                .thenReturn(notificationPage);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        Page<NotificationDto> result = notificationService.getNotificationsForCurrentUser(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Notification", result.getContent().get(0).getTitle());
        verify(notificationRepository).findByRecipientOrderByCreatedAtDesc(currentUser, pageable);
    }

    @Test
    void getUnreadNotificationsForCurrentUser_ShouldReturnUnreadNotifications() {
        // Arrange
        List<Notification> unreadNotifications = Arrays.asList(notification);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(currentUser))
                .thenReturn(unreadNotifications);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        List<NotificationDto> result = notificationService.getUnreadNotificationsForCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());
        verify(notificationRepository).findByRecipientAndIsReadFalseOrderByCreatedAtDesc(currentUser);
    }

    @Test
    void getNotificationsByType_ShouldReturnNotificationsByType() {
        // Arrange
        List<Notification> typeNotifications = Arrays.asList(notification);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findByRecipientAndNotificationTypeOrderByCreatedAtDesc(currentUser, "comment"))
                .thenReturn(typeNotifications);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        List<NotificationDto> result = notificationService.getNotificationsByType("comment");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("comment", result.get(0).getNotificationType());
        verify(notificationRepository).findByRecipientAndNotificationTypeOrderByCreatedAtDesc(currentUser, "comment");
    }

    @Test
    void getNotificationsByCategory_ShouldReturnNotificationsByCategory() {
        // Arrange
        List<Notification> categoryNotifications = Arrays.asList(notification);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findByRecipientAndCategoryOrderByCreatedAtDesc(currentUser, "social"))
                .thenReturn(categoryNotifications);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        List<NotificationDto> result = notificationService.getNotificationsByCategory("social");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("social", result.get(0).getCategory());
        verify(notificationRepository).findByRecipientAndCategoryOrderByCreatedAtDesc(currentUser, "social");
    }

    @Test
    void markNotificationAsRead_ShouldMarkNotificationAsRead() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(currentUser);

        // Act
        notificationService.markNotificationAsRead(1L);

        // Assert
        verify(notificationRepository).markAsRead(eq(1L), any(Instant.class));
    }

    @Test
    void markAllNotificationsAsRead_ShouldMarkAllNotificationsAsRead() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(currentUser);

        // Act
        notificationService.markAllNotificationsAsRead();

        // Assert
        verify(notificationRepository).markAllAsRead(eq(currentUser), any(Instant.class));
    }

    @Test
    void getUnreadNotificationCount_ShouldReturnCorrectCount() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.countByRecipientAndIsReadFalse(currentUser)).thenReturn(5L);

        // Act
        Long result = notificationService.getUnreadNotificationCount();

        // Assert
        assertEquals(5L, result);
        verify(notificationRepository).countByRecipientAndIsReadFalse(currentUser);
    }

    @Test
    void deleteNotification_ShouldDeleteNotificationSuccessfully() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.deleteNotification(1L);

        // Assert
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(notification);
        assertTrue(notification.isDeleted());
        assertNotNull(notification.getDeletedAt());
    }

    @Test
    void deleteNotification_ShouldThrowException_WhenNotificationNotFound() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> notificationService.deleteNotification(999L));
        verify(notificationRepository).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteNotification_ShouldThrowException_WhenUserNotOwner() {
        // Arrange
        User otherUser = new User();
        otherUser.setUserId(999L);
        notification.setRecipient(otherUser);

        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> notificationService.deleteNotification(1L));
        verify(notificationRepository).findById(1L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteAllNotifications_ShouldDeleteAllNotifications() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(currentUser);

        // Act
        notificationService.deleteAllNotifications();

        // Assert
        verify(notificationRepository).deleteAllForUser(eq(currentUser), any(Instant.class));
    }

    @Test
    void createCommentNotification_ShouldCreateCommentNotification() {
        // Arrange
        when(notificationMapper.mapToEntity(any(NotificationDto.class), eq(senderUser), eq(currentUser)))
                .thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        notificationService.createCommentNotification(currentUser, senderUser, 1L, 1L);

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).mapToDto(notification);
    }

    @Test
    void createCommentNotification_ShouldNotCreateNotification_WhenCommenterIsPostOwner() {
        // Act
        notificationService.createCommentNotification(currentUser, currentUser, 1L, 1L);

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(notificationMapper, never()).mapToDto(any(Notification.class));
    }

    @Test
    void createFollowNotification_ShouldCreateFollowNotification() {
        // Arrange
        when(notificationMapper.mapToEntity(any(NotificationDto.class), eq(senderUser), eq(currentUser)))
                .thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        notificationService.createFollowNotification(currentUser, senderUser);

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).mapToDto(notification);
    }

    @Test
    void createFollowNotification_ShouldNotCreateNotification_WhenFollowingSelf() {
        // Act
        notificationService.createFollowNotification(currentUser, currentUser);

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(notificationMapper, never()).mapToDto(any(Notification.class));
    }

    @Test
    void createLikeNotification_ShouldCreateLikeNotification() {
        // Arrange
        when(notificationMapper.mapToEntity(any(NotificationDto.class), eq(senderUser), eq(currentUser)))
                .thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        notificationService.createLikeNotification(currentUser, senderUser, 1L);

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).mapToDto(notification);
    }

    @Test
    void createLikeNotification_ShouldNotCreateNotification_WhenLikingOwnPost() {
        // Act
        notificationService.createLikeNotification(currentUser, currentUser, 1L);

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(notificationMapper, never()).mapToDto(any(Notification.class));
    }

    @Test
    void createMatchNotification_ShouldCreateMatchNotification() {
        // Arrange
        when(notificationMapper.mapToEntity(any(NotificationDto.class), eq(currentUser), eq(senderUser)))
                .thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        notificationService.createMatchNotification(currentUser, senderUser, 1L);

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).mapToDto(notification);
    }

    @Test
    void createCallRequestNotification_ShouldCreateCallRequestNotification() {
        // Arrange
        when(notificationMapper.mapToEntity(any(NotificationDto.class), eq(senderUser), eq(currentUser)))
                .thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        notificationService.createCallRequestNotification(currentUser, senderUser, 1L);

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).mapToDto(notification);
    }

    @Test
    void createSystemNotification_ShouldCreateSystemNotification() {
        // Arrange
        when(notificationMapper.mapToEntity(any(NotificationDto.class), eq(currentUser), eq(null)))
                .thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.mapToDto(notification)).thenReturn(notificationDto);

        // Act
        notificationService.createSystemNotification(currentUser, "System Update", "System is updated", "system");

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).mapToDto(notification);
    }
} 