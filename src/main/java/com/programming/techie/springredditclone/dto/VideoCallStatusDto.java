package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallStatusDto {
    
    private String sessionId;
    private String callStatus; // initiated, ringing, answered, ended, missed, declined, connecting, connected, reconnecting
    private Instant statusUpdatedAt;
    
    // Call Session Information
    private Long callerId;
    private Long receiverId;
    private Long matchId;
    private String callType; // video, voice
    private Instant callStartedAt;
    private Instant callEndedAt;
    private Long callDuration; // Duration in seconds
    
    // Video Call Settings
    private boolean videoEnabled = true;
    private boolean audioEnabled = true;
    private boolean screenSharingEnabled = false;
    private boolean recordingEnabled = false;
    private String videoQuality; // low, medium, high, ultra
    private String audioQuality; // low, medium, high
    
    // Connection Quality
    private String connectionQuality; // excellent, good, fair, poor
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    private String bandwidth; // Available bandwidth
    private String latency; // Connection latency
    private String packetLoss; // Packet loss percentage
    private String jitter; // Network jitter
    
    // Video Stream Information
    private String videoResolution; // e.g., "1920x1080"
    private String videoFrameRate; // e.g., "30fps"
    private String videoCodec; // e.g., "H.264", "VP8", "VP9"
    private String audioCodec; // e.g., "Opus", "AAC"
    private String audioSampleRate; // e.g., "48kHz"
    private String audioChannels; // e.g., "stereo"
    
    // User Status
    private String callerStatus; // online, offline, busy, away, in-call
    private String receiverStatus; // online, offline, busy, away, in-call
    private boolean callerConnected = false;
    private boolean receiverConnected = false;
    private Instant callerJoinedAt;
    private Instant receiverJoinedAt;
    
    // Call Controls
    private boolean callerMuted = false;
    private boolean receiverMuted = false;
    private boolean callerVideoEnabled = true;
    private boolean receiverVideoEnabled = true;
    private String callerCamera = "front"; // front, back
    private String receiverCamera = "front"; // front, back
    
    // Call Statistics
    private Long totalCalls = 0L;
    private Long successfulCalls = 0L;
    private Long missedCalls = 0L;
    private Long declinedCalls = 0L;
    private Long totalCallDuration = 0L;
    private Instant lastCallAt;
    private String lastCallStatus;
    
    // Error Information
    private String errorCode;
    private String errorMessage;
    private String errorDetails;
    private boolean hasError = false;
    
    // WebRTC Information
    private String roomId; // WebRTC room identifier
    private String peerId; // WebRTC peer identifier
    private String signalingServer; // WebRTC signaling server URL
    private String stunServer; // STUN server URL
    private String turnServer; // TURN server URL
    private boolean encrypted = true;
    private String encryptionType; // Type of encryption used
    
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
    private boolean secureCall = true;
    private String securityLevel; // low, medium, high, ultra
    
    // Call Duration Limits
    private Long maxCallDuration; // Maximum call duration in seconds
    private Long remainingTime; // Remaining time in seconds
    private boolean timeLimited = false;
    
    // Call Quality Metrics
    private Double videoQualityScore; // 0.0 to 1.0
    private Double audioQualityScore; // 0.0 to 1.0
    private Double overallQualityScore; // 0.0 to 1.0
    private String qualityRecommendation; // Recommendations for better quality
} 