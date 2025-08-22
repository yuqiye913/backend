package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.VideoCallRequestDto;
import com.programming.techie.springredditclone.dto.VideoCallResponseDto;
import com.programming.techie.springredditclone.dto.VideoCallStatusDto;
import com.programming.techie.springredditclone.dto.VideoCallSessionDto;

import java.util.List;

public interface VideoCallService {
    
    /**
     * Initiate a video call between matched users
     * @param request Video call request details
     * @return Video call session information
     */
    VideoCallSessionDto initiateVideoCall(VideoCallRequestDto request);
    
    /**
     * Accept an incoming video call
     * @param sessionId Video call session ID
     * @return Updated session information
     */
    VideoCallSessionDto acceptVideoCall(String sessionId);
    
    /**
     * Decline an incoming video call
     * @param sessionId Video call session ID
     * @param reason Reason for declining
     */
    void declineVideoCall(String sessionId, String reason);
    
    /**
     * End an active video call
     * @param sessionId Video call session ID
     * @return Final session information
     */
    VideoCallSessionDto endVideoCall(String sessionId);
    
    /**
     * Get video call status for a session
     * @param sessionId Video call session ID
     * @return Current status information
     */
    VideoCallStatusDto getVideoCallStatus(String sessionId);
    
    /**
     * Get active video calls for a user
     * @param userId User ID
     * @return List of active video call sessions
     */
    List<VideoCallSessionDto> getActiveVideoCalls(Long userId);
    
    /**
     * Get video call history for a user
     * @param userId User ID
     * @return List of past video call sessions
     */
    List<VideoCallSessionDto> getVideoCallHistory(Long userId);
    
    /**
     * Check if users can make video calls (must be matched and accepted)
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if video calling is allowed
     */
    boolean canMakeVideoCall(Long userId1, Long userId2);
    
    /**
     * Generate WebRTC signaling information for video call
     * @param sessionId Video call session ID
     * @return WebRTC signaling data
     */
    VideoCallResponseDto generateSignalingData(String sessionId);
    
    /**
     * Update video call session with signaling data
     * @param sessionId Video call session ID
     * @param signalingData WebRTC signaling data
     */
    void updateSignalingData(String sessionId, String signalingData);
    
    /**
     * Mute/unmute audio in video call
     * @param sessionId Video call session ID
     * @param muted Whether audio is muted
     */
    void setAudioMuted(String sessionId, boolean muted);
    
    /**
     * Enable/disable video in video call
     * @param sessionId Video call session ID
     * @param enabled Whether video is enabled
     */
    void setVideoEnabled(String sessionId, boolean enabled);
    
    /**
     * Switch camera in video call
     * @param sessionId Video call session ID
     */
    void switchCamera(String sessionId);
    
    /**
     * Get video call statistics
     * @param sessionId Video call session ID
     * @return Call statistics
     */
    VideoCallStatusDto getCallStatistics(String sessionId);
} 