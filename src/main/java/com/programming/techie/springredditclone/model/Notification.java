package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", referencedColumnName = "userId")
    private User recipient; // User who receives the notification
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "userId")
    private User sender; // User who triggered the notification (can be null for system notifications)
    
    private String title; // Notification title
    private String message; // Notification message
    private String notificationType; // comment, follow, like, system, match, call_request, etc.
    private String priority; // low, normal, high, urgent
    
    // Notification status
    private boolean isRead = false;
    private Instant readAt;
    private boolean isDeleted = false;
    private Instant deletedAt;
    
    // Related content references (optional)
    private Long relatedPostId; // If notification is about a post
    private Long relatedCommentId; // If notification is about a comment
    private Long relatedUserId; // If notification is about a user
    private Long relatedMatchId; // If notification is about a match
    private Long relatedCallId; // If notification is about a call
    
    // Metadata
    private Instant createdAt;
    private String actionUrl; // URL to navigate to when notification is clicked
    private String icon; // Icon to display with notification
    private String category; // social, system, matching, calls, etc.
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        isRead = false;
        isDeleted = false;
        priority = "normal";
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (isRead && readAt == null) {
            readAt = Instant.now();
        }
        if (isDeleted && deletedAt == null) {
            deletedAt = Instant.now();
        }
    }
} 