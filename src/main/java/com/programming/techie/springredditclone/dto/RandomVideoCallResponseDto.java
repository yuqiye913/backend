package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RandomVideoCallResponseDto {
    
    private String requestId; // Unique request ID for tracking
    private String queueStatus; // waiting, matched, connected, timeout, cancelled
    private Long queuePosition; // Position in queue (if waiting)
    private Long estimatedWaitTime; // Estimated wait time in seconds
    
    // Match Information (when matched)
    private String sessionId; // Video call session ID
    private Long matchedUserId; // ID of the matched user
    private String matchedUsername; // Username of matched user
    private String matchedDisplayName; // Display name of matched user
    private String matchedProfilePicture; // Profile picture of matched user
    
    // Call Settings
    private String callType; // video, voice
    private String callPurpose; // casual, business, emergency, social
    private boolean videoEnabled = true;
    private boolean audioEnabled = true;
    private String videoQuality; // low, medium, high, ultra
    private String audioQuality; // low, medium, high
    
    // Connection Information
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String connectionQuality; // excellent, good, fair, poor
    private String bandwidth; // Available bandwidth
    private String latency; // Connection latency
    
    // WebRTC Information (when matched)
    private String roomId; // WebRTC room identifier
    private String peerId; // WebRTC peer identifier
    private String signalingData; // JSON string containing WebRTC signaling
    private String offerSdp; // Session Description Protocol offer
    private String answerSdp; // Session Description Protocol answer
    private String iceCandidates; // ICE candidates for WebRTC
    private String signalingServer; // WebRTC signaling server URL
    private String stunServer; // STUN server URL
    private String turnServer; // TURN server URL
    
    // Match Quality
    private Double matchScore; // 0.0 to 1.0 - how well users match
    private String matchReason; // Why these users were matched
    private String commonInterests; // Shared interests between users
    private String languageCompatibility; // Language compatibility score
    
    // Queue Statistics
    private Long totalUsersInQueue; // Total users waiting
    private Long averageWaitTime; // Average wait time in seconds
    private String queueType; // random, filtered, premium
    private boolean isPriority; // Whether user has priority
    
    // Manual setters for Lombok compatibility
    public void setIsPriority(boolean isPriority) {
        this.isPriority = isPriority;
    }
    
    public boolean getIsPriority() {
        return this.isPriority;
    }
    
    // Timing Information
    private Instant requestCreatedAt;
    private Instant matchedAt; // When match was found
    private Instant callStartedAt; // When call actually started
    private Long timeInQueue; // Time spent in queue in seconds
    
    // Error Information
    private String errorCode;
    private String errorMessage;
    private boolean hasError = false;
    
    // User Preferences (for matching)
    private String preferredGender;
    private String preferredAgeRange;
    private String preferredLanguage;
    private String preferredLocation;
    private String preferredInterests;
    
    // Call Controls
    private boolean canMute = true;
    private boolean canToggleVideo = true;
    private boolean canSwitchCamera = true;
    private boolean canScreenShare = false;
    private boolean canRecord = false;
    
    // Privacy and Security
    private String privacyLevel; // public, private, friends-only, matched-only
    private boolean enableNotifications = true;
    private boolean encrypted = true;
    private String encryptionType; // Type of encryption used
    private boolean secureCall = true;
    private String securityLevel; // low, medium, high, ultra
    
    // Call Duration Limits
    private Long maxCallDuration; // Maximum call duration in seconds
    private Long remainingTime; // Remaining time in seconds
    private boolean timeLimited = false;
    
    // Additional Information
    private String notes; // Additional notes about the call
    private boolean scheduled = false;
    private Instant scheduledTime;
    private String scheduledDuration;
} 