package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCallResponseDto {
    
    private String sessionId;
    private String responseStatus; // accepted, declined, rescheduled, blocked
    private String responseMessage;
    private Instant respondedAt;
    
    // WebRTC Signaling Data
    private String signalingData; // JSON string containing WebRTC signaling
    private String offerSdp; // Session Description Protocol offer
    private String answerSdp; // Session Description Protocol answer
    private String iceCandidates; // ICE candidates for WebRTC
    private String roomId; // WebRTC room identifier
    private String peerId; // WebRTC peer identifier
    
    // Call Session Information
    private Long callerId;
    private Long receiverId;
    private Long matchId;
    private String callType; // video, voice
    private String callStatus; // initiated, ringing, answered, ended, missed, declined
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
    
    // Additional Settings
    private boolean autoAcceptFutureCalls = false;
    private String preferredCallType; // voice, video, both
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean requireAdvanceNotice = false;
    private String advanceNoticePeriod; // How much advance notice is required
    
    // Privacy and Security
    private String privacyLevel; // public, private, friends-only, matched-only
    private boolean enableNotifications = true;
    private boolean enableCallTranscription = false;
    private boolean encrypted = true;
    private String encryptionType; // Type of encryption used
} 