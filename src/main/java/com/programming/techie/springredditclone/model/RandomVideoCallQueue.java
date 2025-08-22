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
@Table(name = "random_video_call_queue")
public class RandomVideoCallQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String requestId; // Unique request identifier
    private String queueStatus; // waiting, matched, connected, timeout, cancelled
    
    // User Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    
    // Call Settings
    private String callType; // video, voice
    private String callPurpose; // casual, business, emergency, social
    private boolean videoEnabled = true;
    private boolean audioEnabled = true;
    private String videoQuality; // low, medium, high, ultra
    private String audioQuality; // low, medium, high
    
    // Connection Settings
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String location;
    private String timezone;
    
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
    private Long queuePosition; // Position in queue
    private Long estimatedWaitTime; // Estimated wait time in seconds
    
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
    private Instant lastActivityAt;
    private Long timeInQueue; // Time spent in queue in seconds
    
    // Match Information (when matched)
    private Long matchedUserId; // ID of the matched user
    private String matchedUsername; // Username of matched user
    private String matchedDisplayName; // Display name of matched user
    private String matchedProfilePicture; // Profile picture of matched user
    private String sessionId; // Video call session ID
    
    // Match Quality
    private Double matchScore; // 0.0 to 1.0 - how well users match
    private String matchReason; // Why these users were matched
    private String commonInterests; // Shared interests between users
    private String languageCompatibility; // Language compatibility score
    
    // WebRTC Information (when matched)
    private String roomId; // WebRTC room identifier
    private String peerId; // WebRTC peer identifier
    @Column(columnDefinition = "TEXT")
    private String signalingData; // JSON string containing WebRTC signaling
    @Column(columnDefinition = "TEXT")
    private String offerSdp; // Session Description Protocol offer
    @Column(columnDefinition = "TEXT")
    private String answerSdp; // Session Description Protocol answer
    @Column(columnDefinition = "TEXT")
    private String iceCandidates; // ICE candidates for WebRTC
    private String signalingServer; // WebRTC signaling server URL
    private String stunServer; // STUN server URL
    private String turnServer; // TURN server URL
    
    // Connection Information
    private String connectionQuality; // excellent, good, fair, poor
    private String bandwidth; // Available bandwidth
    private String latency; // Connection latency
    
    // Call Controls
    private boolean callerMuted = false;
    private boolean receiverMuted = false;
    private boolean callerVideoEnabled = true;
    private boolean receiverVideoEnabled = true;
    private String callerCamera = "front"; // front, back
    private String receiverCamera = "front"; // front, back
    
    // Call Features
    private boolean enableScreenSharing = false;
    private boolean enableRecording = false;
    private boolean enableTranscription = false;
    private boolean enableBackgroundBlur = false;
    private boolean enableNoiseSuppression = true;
    private boolean enableEchoCancellation = true;
    
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
    
    // Error Information
    private String errorCode;
    private String errorMessage;
    @Column(columnDefinition = "TEXT")
    private String errorDetails;
    private boolean hasError = false;
    
    // Additional Settings
    private boolean autoAcceptFutureCalls = false;
    private String preferredCallType; // voice, video, both
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean requireAdvanceNotice = false;
    private String advanceNoticePeriod; // How much advance notice is required
    
    // Call Notes
    @Column(columnDefinition = "TEXT")
    private String notes; // Additional notes about the call
    private String meetingLink; // For scheduled calls
    private String meetingPassword; // For scheduled calls
    private boolean scheduled = false;
    private Instant scheduledTime;
    private String scheduledDuration;
    
    // Queue Statistics
    private Long totalUsersInQueue; // Total users waiting
    private Long averageWaitTime; // Average wait time in seconds
} 