package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.NotificationDto;
import com.programming.techie.springredditclone.model.Notification;
import com.programming.techie.springredditclone.model.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    
    public NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .senderUsername(notification.getSender() != null ? notification.getSender().getUsername() : null)
                .recipientUsername(notification.getRecipient().getUsername())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .priority(notification.getPriority())
                .isRead(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .actionUrl(notification.getActionUrl())
                .icon(notification.getIcon())
                .category(notification.getCategory())
                .relatedPostId(notification.getRelatedPostId())
                .relatedCommentId(notification.getRelatedCommentId())
                .relatedUserId(notification.getRelatedUserId())
                .relatedMatchId(notification.getRelatedMatchId())
                .relatedCallId(notification.getRelatedCallId())
                .build();
    }
    
    public Notification mapToEntity(NotificationDto notificationDto, User recipient, User sender) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setTitle(notificationDto.getTitle());
        notification.setMessage(notificationDto.getMessage());
        notification.setNotificationType(notificationDto.getNotificationType());
        notification.setPriority(notificationDto.getPriority());
        notification.setActionUrl(notificationDto.getActionUrl());
        notification.setIcon(notificationDto.getIcon());
        notification.setCategory(notificationDto.getCategory());
        notification.setRelatedPostId(notificationDto.getRelatedPostId());
        notification.setRelatedCommentId(notificationDto.getRelatedCommentId());
        notification.setRelatedUserId(notificationDto.getRelatedUserId());
        notification.setRelatedMatchId(notificationDto.getRelatedMatchId());
        notification.setRelatedCallId(notificationDto.getRelatedCallId());
        return notification;
    }
} 