package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.VideoCallRequestDto;
import com.programming.techie.springredditclone.dto.VideoCallResponseDto;
import com.programming.techie.springredditclone.dto.VideoCallStatusDto;
import com.programming.techie.springredditclone.dto.VideoCallSessionDto;
import com.programming.techie.springredditclone.service.VideoCallService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/video-calls")
@AllArgsConstructor
public class VideoCallController {

    private final VideoCallService videoCallService;

    /**
     * Initiate a video call between matched users
     */
    @PostMapping("/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoCallSessionDto> initiateVideoCall(@Valid @RequestBody VideoCallRequestDto request) {
        VideoCallSessionDto session = videoCallService.initiateVideoCall(request);
        return ResponseEntity.ok(session);
    }

    /**
     * Accept an incoming video call
     */
    @PostMapping("/{sessionId}/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoCallSessionDto> acceptVideoCall(@PathVariable String sessionId) {
        VideoCallSessionDto session = videoCallService.acceptVideoCall(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Decline an incoming video call
     */
    @PostMapping("/{sessionId}/decline")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> declineVideoCall(
            @PathVariable String sessionId,
            @RequestParam(required = false) String reason) {
        videoCallService.declineVideoCall(sessionId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * End an active video call
     */
    @PostMapping("/{sessionId}/end")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoCallSessionDto> endVideoCall(@PathVariable String sessionId) {
        VideoCallSessionDto session = videoCallService.endVideoCall(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Get video call status
     */
    @GetMapping("/{sessionId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoCallStatusDto> getVideoCallStatus(@PathVariable String sessionId) {
        VideoCallStatusDto status = videoCallService.getVideoCallStatus(sessionId);
        return ResponseEntity.ok(status);
    }

    /**
     * Get active video calls for a user
     */
    @GetMapping("/active/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VideoCallSessionDto>> getActiveVideoCalls(@PathVariable Long userId) {
        List<VideoCallSessionDto> activeCalls = videoCallService.getActiveVideoCalls(userId);
        return ResponseEntity.ok(activeCalls);
    }

    /**
     * Get video call history for a user
     */
    @GetMapping("/history/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VideoCallSessionDto>> getVideoCallHistory(@PathVariable Long userId) {
        List<VideoCallSessionDto> history = videoCallService.getVideoCallHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Check if users can make video calls
     */
    @GetMapping("/can-call")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> canMakeVideoCall(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        boolean canCall = videoCallService.canMakeVideoCall(userId1, userId2);
        return ResponseEntity.ok(canCall);
    }

    /**
     * Generate WebRTC signaling data
     */
    @GetMapping("/{sessionId}/signaling")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoCallResponseDto> generateSignalingData(@PathVariable String sessionId) {
        VideoCallResponseDto signalingData = videoCallService.generateSignalingData(sessionId);
        return ResponseEntity.ok(signalingData);
    }

    /**
     * Update signaling data for a session
     */
    @PutMapping("/{sessionId}/signaling")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateSignalingData(
            @PathVariable String sessionId,
            @RequestBody String signalingData) {
        videoCallService.updateSignalingData(sessionId, signalingData);
        return ResponseEntity.ok().build();
    }

    /**
     * Mute/unmute audio in video call
     */
    @PutMapping("/{sessionId}/audio")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> setAudioMuted(
            @PathVariable String sessionId,
            @RequestParam boolean muted) {
        videoCallService.setAudioMuted(sessionId, muted);
        return ResponseEntity.ok().build();
    }

    /**
     * Enable/disable video in video call
     */
    @PutMapping("/{sessionId}/video")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> setVideoEnabled(
            @PathVariable String sessionId,
            @RequestParam boolean enabled) {
        videoCallService.setVideoEnabled(sessionId, enabled);
        return ResponseEntity.ok().build();
    }

    /**
     * Switch camera in video call
     */
    @PutMapping("/{sessionId}/camera")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> switchCamera(@PathVariable String sessionId) {
        videoCallService.switchCamera(sessionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get call statistics
     */
    @GetMapping("/{sessionId}/statistics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoCallStatusDto> getCallStatistics(@PathVariable String sessionId) {
        VideoCallStatusDto statistics = videoCallService.getCallStatistics(sessionId);
        return ResponseEntity.ok(statistics);
    }
} 