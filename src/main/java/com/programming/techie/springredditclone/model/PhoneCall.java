package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.Duration;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phone_calls")
public class PhoneCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", referencedColumnName = "userId")
    private User caller; // User who initiated the call
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "userId")
    private User receiver; // User who received the call
    
    private Instant callStartedAt;
    private Instant callEndedAt;
    private Duration callDuration;
    private String callType; // voice, video
    private String callStatus; // initiated, ringing, answered, missed, declined, ended
    private String callDirection; // incoming, outgoing
    private boolean isRecorded;
    private String recordingUrl;
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        callStartedAt = Instant.now();
        callStatus = "initiated";
    }
} 