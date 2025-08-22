package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.NotificationMapper;
import com.programming.techie.springredditclone.model.Notification;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.NotificationRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AuthService authService;
    private final BlockService blockService;

    @Override
    public NotificationDto createNotification(NotificationDto notificationDto, User recipient, User sender) {
        Notification notification = notificationMapper.mapToEntity(notificationDto, recipient, sender);
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Created notification: {} for user: {}", notificationDto.getTitle(), recipient.getUsername());
        return notificationMapper.mapToDto(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsForCurrentUser(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        Page<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(currentUser, pageable);
        return notifications.map(notificationMapper::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotificationsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(currentUser);
        return unreadNotifications.stream()
                .map(notificationMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByType(String notificationType) {
        User currentUser = authService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByRecipientAndNotificationTypeOrderByCreatedAtDesc(currentUser, notificationType);
        return notifications.stream()
                .map(notificationMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByCategory(String category) {
        User currentUser = authService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByRecipientAndCategoryOrderByCreatedAtDesc(currentUser, category);
        return notifications.stream()
                .map(notificationMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        User currentUser = authService.getCurrentUser();
        notificationRepository.markAsRead(notificationId, Instant.now());
        log.info("Marked notification {} as read for user: {}", notificationId, currentUser.getUsername());
    }

    @Override
    public void markAllNotificationsAsRead() {
        User currentUser = authService.getCurrentUser();
        notificationRepository.markAllAsRead(currentUser, Instant.now());
        log.info("Marked all notifications as read for user: {}", currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount() {
        User currentUser = authService.getCurrentUser();
        return notificationRepository.countByRecipientAndIsReadFalse(currentUser);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        User currentUser = authService.getCurrentUser();
        // Verify the notification belongs to the current user
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new SpringRedditException("Notification not found"));
        
        if (!notification.getRecipient().equals(currentUser)) {
            throw new SpringRedditException("You can only delete your own notifications");
        }
        
        notification.setDeleted(true);
        notification.setDeletedAt(Instant.now());
        notificationRepository.save(notification);
        log.info("Deleted notification {} for user: {}", notificationId, currentUser.getUsername());
    }

    @Override
    public void deleteAllNotifications() {
        User currentUser = authService.getCurrentUser();
        notificationRepository.deleteAllForUser(currentUser, Instant.now());
        log.info("Deleted all notifications for user: {}", currentUser.getUsername());
    }

    @Override
    public void createCommentNotification(User commenter, User postOwner, Long postId, Long commentId) {
        // Don't notify if user is commenting on their own post
        if (commenter.equals(postOwner)) {
            return;
        }
        
        // Don't notify if users are blocked
        if (blockService.hasBlockedUser(postOwner.getUserId()) || blockService.isBlockedByUser(postOwner.getUserId())) {
            return;
        }
        
        NotificationDto notificationDto = NotificationDto.builder()
                .title("New Comment")
                .message(commenter.getUsername() + " commented on your post")
                .notificationType("comment")
                .category("social")
                .priority("normal")
                .icon("comment-icon")
                .actionUrl("/posts/" + postId + "#comment-" + commentId)
                .relatedPostId(postId)
                .relatedCommentId(commentId)
                .relatedUserId(commenter.getUserId())
                .build();
        
        createNotification(notificationDto, postOwner, commenter);
    }

    @Override
    public void createFollowNotification(User follower, User followed) {
        // Don't notify if user is following themselves
        if (follower.equals(followed)) {
            return;
        }
        
        // Don't notify if users are blocked
        if (blockService.hasBlockedUser(followed.getUserId()) || blockService.isBlockedByUser(followed.getUserId())) {
            return;
        }
        
        NotificationDto notificationDto = NotificationDto.builder()
                .title("New Follower")
                .message(follower.getUsername() + " started following you")
                .notificationType("follow")
                .category("social")
                .priority("normal")
                .icon("follow-icon")
                .actionUrl("/profile/" + follower.getUsername())
                .relatedUserId(follower.getUserId())
                .build();
        
        createNotification(notificationDto, followed, follower);
    }

    @Override
    public void createLikeNotification(User liker, User postOwner, Long postId) {
        // Don't notify if user is liking their own post
        if (liker.equals(postOwner)) {
            return;
        }
        
        // Don't notify if users are blocked
        if (blockService.hasBlockedUser(postOwner.getUserId()) || blockService.isBlockedByUser(postOwner.getUserId())) {
            return;
        }
        
        NotificationDto notificationDto = NotificationDto.builder()
                .title("New Like")
                .message(liker.getUsername() + " liked your post")
                .notificationType("like")
                .category("social")
                .priority("low")
                .icon("like-icon")
                .actionUrl("/posts/" + postId)
                .relatedPostId(postId)
                .relatedUserId(liker.getUserId())
                .build();
        
        createNotification(notificationDto, postOwner, liker);
    }

    @Override
    public void createMatchNotification(User user, User matchedUser, Long matchId) {
        NotificationDto notificationDto = NotificationDto.builder()
                .title("New Match!")
                .message("You matched with " + matchedUser.getUsername())
                .notificationType("match")
                .category("matching")
                .priority("high")
                .icon("match-icon")
                .actionUrl("/matches/" + matchId)
                .relatedMatchId(matchId)
                .relatedUserId(matchedUser.getUserId())
                .build();
        
        createNotification(notificationDto, user, matchedUser);
    }

    @Override
    public void createCallRequestNotification(User caller, User receiver, Long callId) {
        NotificationDto notificationDto = NotificationDto.builder()
                .title("Call Request")
                .message(caller.getUsername() + " wants to call you")
                .notificationType("call_request")
                .category("calls")
                .priority("high")
                .icon("call-icon")
                .actionUrl("/calls/" + callId)
                .relatedCallId(callId)
                .relatedUserId(caller.getUserId())
                .build();
        
        createNotification(notificationDto, receiver, caller);
    }

    @Override
    public void createSystemNotification(User recipient, String title, String message, String category) {
        NotificationDto notificationDto = NotificationDto.builder()
                .title(title)
                .message(message)
                .notificationType("system")
                .category(category)
                .priority("normal")
                .icon("system-icon")
                .build();
        
        createNotification(notificationDto, recipient, null); // System notifications have no sender
    }
} 