package com.programming.techie.springredditclone.controller.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private NotificationDto notificationDto1;
    private NotificationDto notificationDto2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new com.programming.techie.springredditclone.controller.NotificationController(notificationService))
                .build();
        objectMapper = new ObjectMapper();

        // Setup test DTOs
        notificationDto1 = NotificationDto.builder()
                .id(1L)
                .senderUsername("testuser2")
                .recipientUsername("testuser1")
                .title("Comment Notification")
                .message("User2 commented on your post")
                .notificationType("comment")
                .category("social")
                .priority("normal")
                .isRead(false)
                .createdAt(Instant.now())
                .actionUrl("/posts/1")
                .icon("comment-icon")
                .relatedPostId(1L)
                .build();

        notificationDto2 = NotificationDto.builder()
                .id(2L)
                .senderUsername("testuser3")
                .recipientUsername("testuser1")
                .title("Follow Notification")
                .message("User3 started following you")
                .notificationType("follow")
                .category("social")
                .priority("normal")
                .isRead(true)
                .readAt(Instant.now())
                .createdAt(Instant.now().minusSeconds(3600))
                .actionUrl("/profile/user3")
                .icon("follow-icon")
                .relatedUserId(3L)
                .build();
    }

    @Test
    void getNotifications_ShouldReturnPaginatedNotifications() throws Exception {
        // Arrange
        List<NotificationDto> notifications = Arrays.asList(notificationDto1, notificationDto2);
        Page<NotificationDto> notificationPage = new PageImpl<>(notifications, PageRequest.of(0, 20), 2);

        when(notificationService.getNotificationsForCurrentUser(any(Pageable.class)))
                .thenReturn(notificationPage);

        // Act & Assert
        mockMvc.perform(get("/api/notifications")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Comment Notification"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Follow Notification"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(notificationService).getNotificationsForCurrentUser(any(Pageable.class));
    }

    @Test
    void getNotifications_ShouldUseDefaultPagination_WhenNoParameters() throws Exception {
        // Arrange
        List<NotificationDto> notifications = Arrays.asList(notificationDto1);
        Page<NotificationDto> notificationPage = new PageImpl<>(notifications, PageRequest.of(0, 20), 1);

        when(notificationService.getNotificationsForCurrentUser(any(Pageable.class)))
                .thenReturn(notificationPage);

        // Act & Assert
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(notificationService).getNotificationsForCurrentUser(any(Pageable.class));
    }

    @Test
    void getUnreadNotifications_ShouldReturnUnreadNotifications() throws Exception {
        // Arrange
        List<NotificationDto> unreadNotifications = Arrays.asList(notificationDto1);

        when(notificationService.getUnreadNotificationsForCurrentUser())
                .thenReturn(unreadNotifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService).getUnreadNotificationsForCurrentUser();
    }

    @Test
    void getUnreadNotificationCount_ShouldReturnCount() throws Exception {
        // Arrange
        when(notificationService.getUnreadNotificationCount()).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/count/unread"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(notificationService).getUnreadNotificationCount();
    }

    @Test
    void getNotificationsByType_ShouldReturnNotificationsByType() throws Exception {
        // Arrange
        List<NotificationDto> typeNotifications = Arrays.asList(notificationDto1);

        when(notificationService.getNotificationsByType("comment"))
                .thenReturn(typeNotifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/type/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].notificationType").value("comment"));

        verify(notificationService).getNotificationsByType("comment");
    }

    @Test
    void getNotificationsByCategory_ShouldReturnNotificationsByCategory() throws Exception {
        // Arrange
        List<NotificationDto> categoryNotifications = Arrays.asList(notificationDto1, notificationDto2);

        when(notificationService.getNotificationsByCategory("social"))
                .thenReturn(categoryNotifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/category/social"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].category").value("social"))
                .andExpect(jsonPath("$[1].category").value("social"));

        verify(notificationService).getNotificationsByCategory("social");
    }

    @Test
    void markNotificationAsRead_ShouldMarkNotificationAsRead() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification marked as read"));

        verify(notificationService).markNotificationAsRead(1L);
    }

    @Test
    void markAllNotificationsAsRead_ShouldMarkAllNotificationsAsRead() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/notifications/read-all"))
                .andExpect(status().isOk())
                .andExpect(content().string("All notifications marked as read"));

        verify(notificationService).markAllNotificationsAsRead();
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification deleted"));

        verify(notificationService).deleteNotification(1L);
    }

    @Test
    void deleteAllNotifications_ShouldDeleteAllNotifications() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(content().string("All notifications deleted"));

        verify(notificationService).deleteAllNotifications();
    }

    @Test
    void getNotifications_ShouldHandleEmptyResult() throws Exception {
        // Arrange
        Page<NotificationDto> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

        when(notificationService.getNotificationsForCurrentUser(any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(notificationService).getNotificationsForCurrentUser(any(Pageable.class));
    }

    @Test
    void getUnreadNotifications_ShouldHandleEmptyResult() throws Exception {
        // Arrange
        when(notificationService.getUnreadNotificationsForCurrentUser())
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(notificationService).getUnreadNotificationsForCurrentUser();
    }

    @Test
    void getUnreadNotificationCount_ShouldReturnZero_WhenNoUnreadNotifications() throws Exception {
        // Arrange
        when(notificationService.getUnreadNotificationCount()).thenReturn(0L);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/count/unread"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        verify(notificationService).getUnreadNotificationCount();
    }

    @Test
    void getNotificationsByType_ShouldHandleEmptyResult() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByType("nonexistent"))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/notifications/type/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(notificationService).getNotificationsByType("nonexistent");
    }

    @Test
    void getNotificationsByCategory_ShouldHandleEmptyResult() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByCategory("nonexistent"))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/notifications/category/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(notificationService).getNotificationsByCategory("nonexistent");
    }

    @Test
    void getNotifications_ShouldHandleCustomPagination() throws Exception {
        // Arrange
        List<NotificationDto> notifications = Arrays.asList(notificationDto1);
        Page<NotificationDto> notificationPage = new PageImpl<>(notifications, PageRequest.of(1, 5), 6);

        when(notificationService.getNotificationsForCurrentUser(any(Pageable.class)))
                .thenReturn(notificationPage);

        // Act & Assert
        mockMvc.perform(get("/api/notifications")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(6));

        verify(notificationService).getNotificationsForCurrentUser(any(Pageable.class));
    }
} 