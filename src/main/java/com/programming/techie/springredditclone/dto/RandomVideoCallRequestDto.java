package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RandomVideoCallRequestDto {
    
    // User doesn't specify receiver - system will find one
    private Long userId; // Current user making the request
    
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
    private String username;
    private String displayName;
    private String profilePicture;
    
    // Matching Preferences
    private String preferredGender; // male, female, any
    private String preferredAgeRange; // 18-25, 26-35, 36-45, 46+, any
    private String preferredLanguage; // en, es, fr, etc.
    private String preferredLocation; // country or region
    private String preferredInterests; // comma-separated interests
    
    // Queue Settings
    private boolean isPriority = false; // VIP users get priority
    private String queueType = "random"; // random, filtered, premium
    private Long maxWaitTime = 300L; // Maximum wait time in seconds (5 minutes)
    
    // Additional Settings
    private String notes; // Additional notes about the call
    private boolean isScheduled = false;
    private Instant scheduledTime;
    private String scheduledDuration;
    
    // Call Permissions
    private boolean canInitiateCall = true;
    private boolean canReceiveCall = true;
    private boolean canScheduleCall = true;
    private boolean canBlockCall = true;
    
    // Privacy Settings
    private String privacyLevel = "public"; // public, private, friends-only, matched-only
    private boolean enableNotifications = true;
    private boolean enableCallTranscription = false;
} 