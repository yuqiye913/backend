package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchCallConnectionDto {
    private Long connectionId;
    
    @NotNull(message = "Match ID is required")
    private Long matchId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Matched user ID is required")
    private Long matchedUserId;
    
    // Connection Status
    private boolean callingEnabled; // Whether calling is enabled for this match
    private boolean connectionActive; // Whether the connection is currently active
    private String connectionStatus; // active, inactive, blocked, pending
    private Instant connectionActivatedAt; // When calling was enabled
    private Instant lastConnectionAt; // Last time connection was used
    
    // Call Activation Settings
    private boolean autoEnableCalling; // Whether to auto-enable calling when match is accepted
    private boolean requireMutualAcceptance; // Whether both users must accept to enable calling
    private boolean enableImmediateCalling; // Whether calling is available immediately after match
    private String activationTrigger; // manual, automatic, scheduled, conditional
    
    // Call Permissions
    private boolean canInitiateCall; // Whether user can initiate calls
    private boolean canReceiveCall; // Whether user can receive calls
    private boolean canScheduleCall; // Whether user can schedule calls
    private boolean canBlockCall; // Whether user can block calls
    
    // Call Preferences
    private String preferredCallType; // voice, video, both
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls; // Whether to auto-accept calls
    private boolean requireAdvanceNotice; // Whether advance notice is required
    private String advanceNoticePeriod; // How much advance notice is required
    
    // Connection Quality
    private String connectionQuality; // excellent, good, fair, poor
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String locationProximity; // nearby, same-city, same-country, international
    
    // Call History Summary
    private Long totalCalls; // Total number of calls made
    private Long successfulCalls; // Number of successful calls
    private Long missedCalls; // Number of missed calls
    private Long declinedCalls; // Number of declined calls
    private Long totalCallDuration; // Total call duration in seconds
    private Instant lastCallAt; // When the last call was made
    private String lastCallStatus; // completed, missed, declined
    
    // Connection Settings
    private boolean enableNotifications; // Whether to enable call notifications
    private boolean enableCallRecording; // Whether to enable call recording
    private boolean enableCallTranscription; // Whether to enable call transcription
    private String privacyLevel; // public, private, friends-only, matched-only
    
    // User Information
    private String userUsername;
    private String userDisplayName;
    private String userProfilePicture;
    private String matchedUserUsername;
    private String matchedUserDisplayName;
    private String matchedUserProfilePicture;
    
    // Match Information
    private String matchType; // friendship, relationship, networking, casual
    private Double matchScore; // Overall match score
    private String matchStatus; // pending, accepted, declined, blocked
    
    // Connection Metadata
    private String connectionNotes; // Additional notes about the connection
    private String connectionTags; // Tags for categorizing the connection
    private boolean isVerifiedConnection; // Whether this is a verified connection
    private String verificationMethod; // How the connection was verified
    
    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt; // When the connection expires (if applicable)
} 