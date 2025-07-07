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
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "userId")
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "userId")
    private User receiver;
    
    private String message;
    private Instant requestedAt;
    private String status; // pending, accepted, declined, cancelled
    private String requestType; // friend, acquaintance, colleague, etc.
    private boolean isRead;
    private Instant readAt;
    
    @PrePersist
    protected void onCreate() {
        requestedAt = Instant.now();
        status = "pending";
        isRead = false;
    }
} 