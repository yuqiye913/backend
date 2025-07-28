package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallRequestDto {
    
    @NotNull(message = "Caller ID is required")
    private Long callerId;
    
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
    
    @NotNull(message = "Match ID is required")
    private Long matchId;
    
    // Call Settings
    private String callType = "video"; // video, voice
    private String callPurpose = "casual"; // casual, business, emergency, social
    private String preferredCallTime = "anytime"; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls = false;
    private boolean requireAdvanceNotice = false;
    private String advanceNoticePeriod = "30 minutes";
    
    // Video Call Settings
    private boolean enableVideo = true;
    private boolean enableAudio = true;
    private boolean enableScreenSharing = false;
    private boolean enableRecording = false;
    private String videoQuality = "high"; // low, medium, high, ultra
    private String audioQuality = "high"; // low, medium, high
    
    // Connection Settings
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String location;
    private String timezone;
    
    // User Information
    private String callerUsername;
    private String callerDisplayName;
    private String callerProfilePicture;
    private String receiverUsername;
    private String receiverDisplayName;
    private String receiverProfilePicture;
    
    // Match Information
    private String matchType; // friendship, relationship, networking, casual
    private Double matchScore;
    private String matchStatus;
    
    // Call History Context
    private Long previousCallsCount = 0L;
    private Instant lastCallAt;
    private String lastCallStatus;
    private Long lastCallDuration;
    
    // Additional Settings
    private String notes; // Additional notes about the call
    private String meetingLink; // For scheduled calls
    private String meetingPassword; // For scheduled calls
    private boolean isScheduled = false;
    private Instant scheduledTime;
    private String scheduledDuration;
    
    // Call Permissions
    private boolean canInitiateCall = true;
    private boolean canReceiveCall = true;
    private boolean canScheduleCall = true;
    private boolean canBlockCall = true;
    
    // Privacy Settings
    private String privacyLevel = "matched-only"; // public, private, friends-only, matched-only
    private boolean enableNotifications = true;
    private boolean enableCallTranscription = false;
} 