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
@Table(name = "friend_responses")
public class FriendResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "requestId")
    private FriendRequest request;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id", referencedColumnName = "userId")
    private User responder;
    
    private String responseStatus; // accepted, declined, blocked
    private String responseMessage;
    private Instant respondedAt;
    private String relationshipType; // friend, acquaintance, colleague, etc.
    private boolean isRead;
    private Instant readAt;
    private boolean autoFollow;
    private boolean allowDirectMessages;
    private boolean allowPhoneCalls;
    
    @PrePersist
    protected void onCreate() {
        respondedAt = Instant.now();
        isRead = false;
        autoFollow = false;
        allowDirectMessages = true;
        allowPhoneCalls = false;
    }
} 