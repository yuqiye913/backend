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
@Table(name = "calling_responses")
public class CallingResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "requestId")
    private CallingRequest request;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id", referencedColumnName = "userId")
    private User responder;
    
    private String responseStatus; // accepted, declined, rescheduled, blocked
    private String responseMessage;
    private Instant respondedAt;
    private Instant proposedTime;
    private String proposedDuration;
    private String declineReason;
    private boolean isRead;
    private Instant readAt;
    private boolean autoAcceptFutureCalls;
    private String preferredCallType;
    private String preferredCallTime;
    private boolean requireAdvanceNotice;
    private String advanceNoticePeriod;
    
    @PrePersist
    protected void onCreate() {
        respondedAt = Instant.now();
        isRead = false;
        autoAcceptFutureCalls = false;
        requireAdvanceNotice = false;
    }
} 