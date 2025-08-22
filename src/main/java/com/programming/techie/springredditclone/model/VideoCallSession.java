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
@Table(name = "video_call_sessions")
public class VideoCallSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String sessionId;
    private String callStatus; // initiated, ringing, answered, ended, missed, declined, connecting, connected, reconnecting
    private Instant createdAt;
    private Instant updatedAt;
    
    // Call Participants
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", referencedColumnName = "userId")
    private User caller;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "userId")
    private User receiver;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", referencedColumnName = "matchId")
    private Match match;
    
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
    private boolean isEncrypted = true;
    private String encryptionType; // Type of encryption used
    private boolean isSecureCall = true;
    private String securityLevel; // low, medium, high, ultra
    
    // Call Duration Limits
    private Long maxCallDuration; // Maximum call duration in seconds
    private Long remainingTime; // Remaining time in seconds
    private boolean isTimeLimited = false;
    
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
    private boolean isScheduled = false;
    private Instant scheduledTime;
    private String scheduledDuration;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        callStatus = "initiated";
        videoEnabled = true;
        audioEnabled = true;
        callerMuted = false;
        receiverMuted = false;
        callerVideoEnabled = true;
        receiverVideoEnabled = true;
        enableNotifications = true;
        isEncrypted = true;
        isSecureCall = true;
        enableNoiseSuppression = true;
        enableEchoCancellation = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
} 