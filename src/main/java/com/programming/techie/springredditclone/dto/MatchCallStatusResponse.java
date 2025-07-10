package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchCallStatusResponse {
    
    private Long matchId;
    private Long userId;
    private Long matchedUserId;
    
    // Connection Status
    private boolean callingEnabled;
    private boolean connectionActive;
    private String connectionStatus; // active, inactive, blocked, pending
    private Instant connectionActivatedAt;
    private Instant lastConnectionAt;
    
    // Call Availability
    private boolean canInitiateCall;
    private boolean canReceiveCall;
    private String callingStatus; // available, busy, offline, blocked
    private String availabilityMessage; // Custom availability message
    
    // Call Statistics
    private Long totalCalls;
    private Long successfulCalls;
    private Long missedCalls;
    private Long declinedCalls;
    private Long totalCallDuration;
    private Instant lastCallAt;
    private String lastCallStatus;
    
    // Connection Quality
    private String connectionQuality; // excellent, good, fair, poor
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String locationProximity; // nearby, same-city, same-country, international
    
    // User Information
    private String userUsername;
    private String userDisplayName;
    private String userProfilePicture;
    private String userStatus; // online, offline, busy, away
    private String matchedUserUsername;
    private String matchedUserDisplayName;
    private String matchedUserProfilePicture;
    private String matchedUserStatus; // online, offline, busy, away
    
    // Match Information
    private String matchType; // friendship, relationship, networking, casual
    private Double matchScore; // Overall match score
    private String matchStatus; // pending, accepted, declined, blocked
    private boolean isMutualMatch;
    
    // Call Preferences
    private String preferredCallType; // voice, video, both
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls;
    private boolean requireAdvanceNotice;
    private String advanceNoticePeriod;
    
    // Connection Settings
    private boolean enableNotifications;
    private boolean enableCallRecording;
    private boolean enableCallTranscription;
    private String privacyLevel; // public, private, friends-only, matched-only
    
    // Recent Activity
    private Instant lastInteractionAt;
    private String lastInteractionType; // call, message, match, etc.
    private String lastInteractionStatus; // completed, pending, failed
    
    // Connection Health
    private boolean isConnectionHealthy;
    private String connectionHealthMessage;
    private String connectionIssues; // Any issues with the connection
    
    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt; // When the connection expires (if applicable)
} 