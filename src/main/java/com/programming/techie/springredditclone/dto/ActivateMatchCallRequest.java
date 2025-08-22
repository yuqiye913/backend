package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivateMatchCallRequest {
    
    @NotNull(message = "Match ID is required")
    private Long matchId;
    
    // Activation Settings
    private boolean enableImmediateCalling; // Whether to enable calling immediately
    private boolean requireMutualAcceptance; // Whether both users must accept
    private String activationTrigger; // manual, automatic, scheduled, conditional
    
    // Call Preferences
    private String preferredCallType; // voice, video, both
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls; // Whether to auto-accept calls
    private boolean requireAdvanceNotice; // Whether advance notice is required
    private String advanceNoticePeriod; // How much advance notice is required
    
    // Connection Settings
    private boolean enableNotifications; // Whether to enable call notifications
    private boolean enableCallRecording; // Whether to enable call recording
    private boolean enableCallTranscription; // Whether to enable call transcription
    private String privacyLevel; // public, private, friends-only, matched-only
    
    // Call Permissions
    private boolean canInitiateCall; // Whether user can initiate calls
    private boolean canReceiveCall; // Whether user can receive calls
    private boolean canScheduleCall; // Whether user can schedule calls
    private boolean canBlockCall; // Whether user can block calls
    
    // Additional Settings
    private String activationNotes; // Additional notes about activation
    private String activationTags; // Tags for categorizing the activation
    private boolean isVerifiedActivation; // Whether this is a verified activation
} 