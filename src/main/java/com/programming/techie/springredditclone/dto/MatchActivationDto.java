package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchActivationDto {
    
    @NotNull(message = "Match ID is required")
    private Long matchId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Matched user ID is required")
    private Long matchedUserId;
    
    // Match Activation
    private String activationType; // accept, decline, block, super_like
    private boolean isActivated;
    private Instant activatedAt;
    private String activationMessage; // Custom message for activation
    
    // Calling Integration
    private boolean enableCalling; // Whether to enable calling functionality
    private boolean callingEnabled; // Whether calling is actually enabled
    private Instant callingEnabledAt; // When calling was enabled
    private String callingActivationTrigger; // manual, automatic, scheduled, conditional
    
    // Call Settings
    private String preferredCallType; // voice, video, both
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls; // Whether to auto-accept calls
    private boolean requireAdvanceNotice; // Whether advance notice is required
    private String advanceNoticePeriod; // How much advance notice is required
    
    // Call Permissions
    private boolean canInitiateCall; // Whether user can initiate calls
    private boolean canReceiveCall; // Whether user can receive calls
    private boolean canScheduleCall; // Whether user can schedule calls
    private boolean canBlockCall; // Whether user can block calls
    
    // Connection Settings
    private boolean enableNotifications; // Whether to enable call notifications
    private boolean enableCallRecording; // Whether to enable call recording
    private boolean enableCallTranscription; // Whether to enable call transcription
    private String privacyLevel; // public, private, friends-only, matched-only
    
    // Match Information
    private String matchType; // friendship, relationship, networking, casual
    private Double matchScore; // Overall match score
    private String matchStatus; // pending, accepted, declined, blocked
    private boolean isMutualMatch;
    private boolean isSuperLike;
    private boolean isVerifiedMatch;
    
    // User Information
    private String userUsername;
    private String userDisplayName;
    private String userProfilePicture;
    private String userStatus; // online, offline, busy, away
    private String matchedUserUsername;
    private String matchedUserDisplayName;
    private String matchedUserProfilePicture;
    private String matchedUserStatus; // online, offline, busy, away
    
    // Connection Quality
    private String connectionQuality; // excellent, good, fair, poor
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String locationProximity; // nearby, same-city, same-country, international
    
    // Activation Preferences
    private boolean requireMutualAcceptance; // Whether both users must accept
    private boolean enableImmediateCalling; // Whether calling is available immediately
    private boolean enableScheduledCalling; // Whether scheduled calling is enabled
    private String activationSchedule; // JSON string for activation schedule
    
    // Communication Preferences
    private String preferredCommunicationMethod; // text, voice, video, in-person
    private String preferredMeetingType; // coffee, dinner, activity, virtual
    private String availability; // weekday evenings, weekends, flexible
    
    // Match Details
    private String matchReason; // Why these users were matched
    private String commonInterests; // Comma-separated common interests
    private String commonHobbies; // Comma-separated common hobbies
    private String commonValues; // Comma-separated common values
    private Integer ageDifference; // Age difference between users
    private String locationDistance; // Distance between users
    private String timezoneDifference; // Timezone difference
    
    // Activation Metadata
    private String activationNotes; // Additional notes about activation
    private String activationTags; // Tags for categorizing the activation
    private boolean isVerifiedActivation; // Whether this is a verified activation
    private String verificationMethod; // How the activation was verified
    
    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt; // When the match expires (if applicable)
    
    // Response Information
    private boolean success; // Whether the activation was successful
    private String message; // Response message
    private String errorCode; // Error code if activation failed
    private String errorMessage; // Error message if activation failed
} 