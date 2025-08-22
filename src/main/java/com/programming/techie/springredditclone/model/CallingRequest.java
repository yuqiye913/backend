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
@Table(name = "calling_requests")
public class CallingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", referencedColumnName = "userId")
    private User caller;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "userId")
    private User receiver;
    
    private String callType; // voice, video
    private String callPurpose; // casual, business, emergency, etc.
    private Instant requestedAt;
    private String status; // pending, accepted, declined, missed, cancelled
    private String priority; // low, normal, high, urgent
    private boolean isScheduled;
    private Instant scheduledTime;
    private String scheduledDuration; // Expected duration in minutes
    private String notes;
    private boolean isRead;
    private Instant readAt;
    private boolean requireConfirmation;
    private String meetingLink;
    private String meetingPassword;
    
    @PrePersist
    protected void onCreate() {
        requestedAt = Instant.now();
        status = "pending";
        isRead = false;
        requireConfirmation = false;
        isScheduled = false;
        priority = "normal";
    }
} 