package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallSessionDto {
    
    private String sessionId;
    private String callStatus; // initiated, ringing, answered, ended, missed, declined, connecting, connected, reconnecting
    private Instant createdAt;
    private Instant updatedAt;
    
    // Call Participants
    private Long callerId;
    private Long receiverId;
    private Long matchId;
    private String callType; // video, voice
    private String callPurpose; // casual, business, emergency, social
    
    // Call Timing
    private Instant callStartedAt;
    private Instant callEndedAt;
    private Long callDuration; // Duration in seconds
    private Instant lastActivityAt;
    
    // Video Call Settings
    private boolean videoEnabled = true;
    private boolean audioEnabled = true;
    private boolean screenSharingEnabled = false;
    private boolean recordingEnabled = false;
    private String videoQuality; // low, medium, high, ultra
    private String audioQuality; // low, medium, high
    
    // Connection Information
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String connectionQuality; // excellent, good, fair, poor
    private String bandwidth; // Available bandwidth
    private String latency; // Connection latency
    
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
    
    // Call Controls
    private boolean callerMuted = false;
    private boolean receiverMuted = false;
    private boolean callerVideoEnabled = true;
    private boolean receiverVideoEnabled = true;
    private String callerCamera = "front"; // front, back
    private String receiverCamera = "front"; // front, back
    
    // WebRTC Information
    private String roomId; // WebRTC room identifier
    private String peerId; // WebRTC peer identifier
    private String signalingData; // JSON string containing WebRTC signaling
    private String offerSdp; // Session Description Protocol offer
    private String answerSdp; // Session Description Protocol answer
    private String iceCandidates; // ICE candidates for WebRTC
    private String signalingServer; // WebRTC signaling server URL
    private String stunServer; // STUN server URL
    private String turnServer; // TURN server URL
    
    // Call Statistics
    private Long totalCalls = 0L;
    private Long successfulCalls = 0L;
    private Long missedCalls = 0L;
    private Long declinedCalls = 0L;
    private Long totalCallDuration = 0L;
    private Instant lastCallAt;
    private String lastCallStatus;
    
    // Call Quality Metrics
    private Double videoQualityScore; // 0.0 to 1.0
    private Double audioQualityScore; // 0.0 to 1.0
    private Double overallQualityScore; // 0.0 to 1.0
    private String qualityRecommendation; // Recommendations for better quality
    
    // Video Stream Information
    private String videoResolution; // e.g., "1920x1080"
    private String videoFrameRate; // e.g., "30fps"
    private String videoCodec; // e.g., "H.264", "VP8", "VP9"
    private String audioCodec; // e.g., "Opus", "AAC"
    private String audioSampleRate; // e.g., "48kHz"
    private String audioChannels; // e.g., "stereo"
    
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
    private String errorDetails;
    private boolean hasError = false;
    
    // Additional Settings
    private boolean autoAcceptFutureCalls = false;
    private String preferredCallType; // voice, video, both
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean requireAdvanceNotice = false;
    private String advanceNoticePeriod; // How much advance notice is required
    
    // Call Notes
    private String notes; // Additional notes about the call
    private String meetingLink; // For scheduled calls
    private String meetingPassword; // For scheduled calls
    private boolean scheduled = false;
    private Instant scheduledTime;
    private String scheduledDuration;
} 