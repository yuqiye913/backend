package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.WebRTCSignalingDto;
import com.programming.techie.springredditclone.dto.WebRTCIceCandidateDto;
import com.programming.techie.springredditclone.dto.WebRTCSessionDescriptionDto;

/**
 * Service for handling WebRTC signaling between video call participants
 */
public interface WebRTCSignalingService {
    
    /**
     * Create a new WebRTC session
     * @param sessionId Video call session ID
     * @return WebRTC session information
     */
    WebRTCSignalingDto createSession(String sessionId);
    
    /**
     * Generate offer SDP for the caller
     * @param sessionId Video call session ID
     * @return Session description for the offer
     */
    WebRTCSessionDescriptionDto generateOffer(String sessionId);
    
    /**
     * Generate answer SDP for the receiver
     * @param sessionId Video call session ID
     * @param offerSdp The offer SDP from the caller
     * @return Session description for the answer
     */
    WebRTCSessionDescriptionDto generateAnswer(String sessionId, String offerSdp);
    
    /**
     * Add ICE candidate for a session
     * @param sessionId Video call session ID
     * @param candidate ICE candidate information
     */
    void addIceCandidate(String sessionId, WebRTCIceCandidateDto candidate);
    
    /**
     * Get ICE candidates for a session
     * @param sessionId Video call session ID
     * @return List of ICE candidates
     */
    java.util.List<WebRTCIceCandidateDto> getIceCandidates(String sessionId);
    
    /**
     * Update session description
     * @param sessionId Video call session ID
     * @param description Session description to update
     */
    void updateSessionDescription(String sessionId, WebRTCSessionDescriptionDto description);
    
    /**
     * Get session description
     * @param sessionId Video call session ID
     * @return Current session description
     */
    WebRTCSessionDescriptionDto getSessionDescription(String sessionId);
    
    /**
     * Close WebRTC session
     * @param sessionId Video call session ID
     */
    void closeSession(String sessionId);
    
    /**
     * Check if session is active
     * @param sessionId Video call session ID
     * @return true if session is active
     */
    boolean isSessionActive(String sessionId);
    
    /**
     * Get STUN/TURN server configuration
     * @return STUN/TURN server information
     */
    WebRTCSignalingDto getServerConfiguration();
} 