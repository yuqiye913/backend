package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchStatusDto {
    
    private Long userId;
    private String username;
    
    // Match Statistics
    private Long totalMatches;
    private Long pendingMatches;
    private Long acceptedMatches;
    private Long declinedMatches;
    private Long mutualMatches;
    
    // Voice Calling Statistics
    private Long totalCalls;
    private Long successfulCalls;
    private Long missedCalls;
    private Long declinedCalls;
    private Long totalCallDuration; // Total call duration in seconds
    private Instant lastCallAt;
    private String lastCallStatus;
    
    // Recent Activity
    private Instant lastMatchAt;
    private Instant lastInteractionAt;
    
    // Voice Calling Preferences
    private String preferredCallType; // voice, video
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls; // Whether to auto-accept calls
    private boolean requireAdvanceNotice; // Whether advance notice is required
    private String advanceNoticePeriod; // How much advance notice is required
    
    private boolean isActive; // Whether user is actively looking for matches
    private String availability; // available, busy, offline, in-call
} 