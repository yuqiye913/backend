package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String senderUsername; // Username of the sender (null for system notifications)
    private String recipientUsername; // Username of the recipient
    private String title;
    private String message;
    private String notificationType; // comment, follow, like, system, match, call_request, etc.
    private String priority; // low, normal, high, urgent
    private boolean isRead;
    private Instant readAt;
    private Instant createdAt;
    private String actionUrl; // URL to navigate to when notification is clicked
    private String icon; // Icon to display with notification
    private String category; // social, system, matching, calls, etc.
    
    // Related content references
    private Long relatedPostId;
    private Long relatedCommentId;
    private Long relatedUserId;
    private Long relatedMatchId;
    private Long relatedCallId;
} 