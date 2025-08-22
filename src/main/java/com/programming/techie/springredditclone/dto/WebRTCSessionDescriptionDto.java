package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebRTCSessionDescriptionDto {
    
    private String sessionId;
    private String type; // offer, answer, pranswer, rollback
    private String sdp; // Session Description Protocol string
    
    // Session Information
    private String sessionType; // video, audio, data
    private String sessionStatus; // active, connecting, connected, closed
    private Long timestamp;
    
    // Media Information
    private String videoCodec; // H.264, VP8, VP9
    private String audioCodec; // Opus, AAC
    private String videoResolution; // 1920x1080, 1280x720, etc.
    private String videoFrameRate; // 30fps, 60fps
    private String audioSampleRate; // 48kHz, 44.1kHz
    private String audioChannels; // mono, stereo
    
    // Quality Settings
    private String videoBitrate; // Video bitrate in kbps
    private String audioBitrate; // Audio bitrate in kbps
    private String videoQuality; // low, medium, high, ultra
    private String audioQuality; // low, medium, high
    
    // Network Settings
    private String networkType; // wifi, cellular, ethernet
    private String bandwidth; // Available bandwidth
    private String latency; // Connection latency
    
    // Security Settings
    private boolean isEncrypted = true;
    private String encryptionType; // DTLS, SRTP
    private String certificate; // DTLS certificate
    private String fingerprint; // DTLS fingerprint
    
    // ICE Settings
    private String iceServers; // JSON string of ICE servers
    private String iceTransportPolicy; // all, relay
    private String bundlePolicy; // balanced, max-bundle, max-compat
    private String rtcpMuxPolicy; // require, negotiate
    
    // Media Constraints
    private String videoConstraints; // JSON string of video constraints
    private String audioConstraints; // JSON string of audio constraints
    private String dataChannelConstraints; // JSON string of data channel constraints
    
    // Connection Information
    private String localDescription;
    private String remoteDescription;
    private String signalingState; // stable, have-local-offer, have-remote-offer, have-local-pranswer, have-remote-pranswer, closed
    private String iceConnectionState; // new, checking, connected, completed, failed, disconnected, closed
    private String iceGatheringState; // new, gathering, complete
    
    // Error Information
    private String errorCode;
    private String errorMessage;
    private boolean hasError = false;
} 