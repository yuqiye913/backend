package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebRTCIceCandidateDto {
    
    private String sessionId;
    private String candidate; // ICE candidate string
    private String sdpMid; // SDP media description ID
    private Integer sdpMLineIndex; // SDP media line index
    private String usernameFragment; // ICE username fragment
    
    // Candidate Information
    private String foundation; // ICE foundation
    private String component; // ICE component (RTP/RTCP)
    private String protocol; // Transport protocol (UDP/TCP)
    private String priority; // ICE priority
    private String ip; // IP address
    private Integer port; // Port number
    private String type; // ICE candidate type (host, srflx, prflx, relay)
    
    // Network Information
    private String networkType; // wifi, cellular, ethernet
    private String networkCost; // low, medium, high
    private String addressFamily; // IPv4, IPv6
    
    // Quality Metrics
    private String bandwidth; // Available bandwidth
    private String latency; // Connection latency
    private String packetLoss; // Packet loss percentage
    private String jitter; // Network jitter
    
    // Security Information
    private boolean isEncrypted = true;
    private String encryptionType; // DTLS, SRTP
    private String certificate; // DTLS certificate
    
    // Timestamp
    private Long timestamp;
    
    // Error Information
    private String errorCode;
    private String errorMessage;
    private boolean hasError = false;
} 