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
@Table(name = "direct_messages")
public class DirectMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "userId")
    private User sender; // User who sent the message
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "userId")
    private User receiver; // User who received the message
    
    private String messageContent;
    private String messageType; // text, image, video, audio, file
    private String mediaUrl; // URL to media file if applicable
    private Instant sentAt;
    private Instant readAt;
    private boolean isRead;
    private boolean isDeleted;
    private boolean isEdited;
    private Instant editedAt;
    private String originalContent; // Store original content if edited
    private boolean isForwarded;
    private Long originalMessageId; // ID of original message if forwarded
    
    @PrePersist
    protected void onCreate() {
        sentAt = Instant.now();
        isRead = false;
        isDeleted = false;
        isEdited = false;
        isForwarded = false;
    }
} 