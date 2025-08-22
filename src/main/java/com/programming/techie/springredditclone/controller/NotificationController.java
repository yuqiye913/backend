package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationDto> notifications = notificationService.getNotificationsForCurrentUser(pageable);
        return new ResponseEntity<>(notifications, OK);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        List<NotificationDto> unreadNotifications = notificationService.getUnreadNotificationsForCurrentUser();
        return new ResponseEntity<>(unreadNotifications, OK);
    }

    @GetMapping("/count/unread")
    public ResponseEntity<Long> getUnreadNotificationCount() {
        Long count = notificationService.getUnreadNotificationCount();
        return new ResponseEntity<>(count, OK);
    }

    @GetMapping("/type/{notificationType}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByType(@PathVariable String notificationType) {
        List<NotificationDto> notifications = notificationService.getNotificationsByType(notificationType);
        return new ResponseEntity<>(notifications, OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByCategory(@PathVariable String category) {
        List<NotificationDto> notifications = notificationService.getNotificationsByCategory(category);
        return new ResponseEntity<>(notifications, OK);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return new ResponseEntity<>("Notification marked as read", OK);
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllNotificationsAsRead() {
        notificationService.markAllNotificationsAsRead();
        return new ResponseEntity<>("All notifications marked as read", OK);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return new ResponseEntity<>("Notification deleted", OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllNotifications() {
        notificationService.deleteAllNotifications();
        return new ResponseEntity<>("All notifications deleted", OK);
    }
} 