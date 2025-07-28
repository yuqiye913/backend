package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebRTCSignalingDto {
    
    private String sessionId;
    private String roomId;
    private String peerId;
    
    // STUN/TURN Server Configuration
    private List<String> stunServers;
    private List<String> turnServers;
    private String turnUsername;
    private String turnCredential;
    
    // Session Information
    private String sessionType; // offer, answer, ice-candidate
    private String sessionStatus; // active, connecting, connected, closed
    private Long timestamp;
    
    // WebRTC Configuration
    private String iceServers; // JSON string of ICE servers
    private String rtcConfiguration; // JSON string of RTCConfiguration
    private String constraints; // JSON string of MediaConstraints
    
    // Connection Information
    private String localDescription;
    private String remoteDescription;
    private List<String> iceCandidates;
    
    // Quality Settings
    private String videoCodec; // H.264, VP8, VP9
    private String audioCodec; // Opus, AAC
    private String videoResolution; // 1920x1080, 1280x720, etc.
    private String videoFrameRate; // 30fps, 60fps
    private String audioSampleRate; // 48kHz, 44.1kHz
    private String audioChannels; // mono, stereo
    
    // Security Settings
    private boolean isEncrypted = true;
    private String encryptionType; // DTLS, SRTP
    private String certificate; // DTLS certificate
    
    // Network Information
    private String networkType; // wifi, cellular, ethernet
    private String bandwidth; // Available bandwidth
    private String latency; // Connection latency
    
    // Error Information
    private String errorCode;
    private String errorMessage;
    private boolean hasError = false;
} 