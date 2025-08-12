package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.WebRTCSignalingDto;
import com.programming.techie.springredditclone.dto.WebRTCIceCandidateDto;
import com.programming.techie.springredditclone.dto.WebRTCSessionDescriptionDto;
import com.programming.techie.springredditclone.service.WebRTCSignalingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/webrtc")
@AllArgsConstructor
@Slf4j
public class WebRTCSignalingController {

    private final WebRTCSignalingService webRTCSignalingService;

    /**
     * Create a new WebRTC signaling session
     */
    @PostMapping("/sessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WebRTCSignalingDto> createSession(@RequestParam String sessionId) {
        WebRTCSignalingDto session = webRTCSignalingService.createSession(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Get server configuration (STUN/TURN servers)
     */
    @GetMapping("/config")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WebRTCSignalingDto> getServerConfiguration() {
        WebRTCSignalingDto config = webRTCSignalingService.getServerConfiguration();
        return ResponseEntity.ok(config);
    }

    /**
     * Generate offer SDP
     */
    @PostMapping("/{sessionId}/offer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WebRTCSessionDescriptionDto> generateOffer(@PathVariable String sessionId) {
        WebRTCSessionDescriptionDto offer = webRTCSignalingService.generateOffer(sessionId);
        return ResponseEntity.ok(offer);
    }

    /**
     * Generate answer SDP
     */
    @PostMapping("/{sessionId}/answer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WebRTCSessionDescriptionDto> generateAnswer(
            @PathVariable String sessionId,
            @RequestBody String offerSdp) {
        WebRTCSessionDescriptionDto answer = webRTCSignalingService.generateAnswer(sessionId, offerSdp);
        return ResponseEntity.ok(answer);
    }

    /**
     * Add ICE candidate
     */
    @PostMapping("/{sessionId}/ice-candidate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addIceCandidate(
            @PathVariable String sessionId,
            @Valid @RequestBody WebRTCIceCandidateDto candidate) {
        webRTCSignalingService.addIceCandidate(sessionId, candidate);
        return ResponseEntity.ok().build();
    }

    /**
     * Get ICE candidates for a session
     */
    @GetMapping("/{sessionId}/ice-candidates")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WebRTCIceCandidateDto>> getIceCandidates(@PathVariable String sessionId) {
        List<WebRTCIceCandidateDto> candidates = webRTCSignalingService.getIceCandidates(sessionId);
        return ResponseEntity.ok(candidates);
    }

    /**
     * Update session description
     */
    @PutMapping("/{sessionId}/session-description")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateSessionDescription(
            @PathVariable String sessionId,
            @Valid @RequestBody WebRTCSessionDescriptionDto description) {
        webRTCSignalingService.updateSessionDescription(sessionId, description);
        return ResponseEntity.ok().build();
    }

    /**
     * Get session description
     */
    @GetMapping("/{sessionId}/session-description")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WebRTCSessionDescriptionDto> getSessionDescription(@PathVariable String sessionId) {
        WebRTCSessionDescriptionDto description = webRTCSignalingService.getSessionDescription(sessionId);
        if (description != null) {
            return ResponseEntity.ok(description);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if session is active
     */
    @GetMapping("/{sessionId}/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isSessionActive(@PathVariable String sessionId) {
        boolean isActive = webRTCSignalingService.isSessionActive(sessionId);
        return ResponseEntity.ok(isActive);
    }

    /**
     * Close WebRTC session
     */
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> closeSession(@PathVariable String sessionId) {
        webRTCSignalingService.closeSession(sessionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Poll for signaling updates (for real-time communication)
     */
    @GetMapping("/{sessionId}/poll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WebRTCSignalingDto> pollForUpdates(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") long lastUpdate) {
        
        // Check if session is active
        if (!webRTCSignalingService.isSessionActive(sessionId)) {
            return ResponseEntity.notFound().build();
        }

        // Get current session info
        WebRTCSessionDescriptionDto description = webRTCSignalingService.getSessionDescription(sessionId);
        List<WebRTCIceCandidateDto> candidates = webRTCSignalingService.getIceCandidates(sessionId);
        
        // Create response with current state
        WebRTCSignalingDto response = new WebRTCSignalingDto();
        response.setSessionId(sessionId);
        response.setSessionStatus("active");
        response.setTimestamp(System.currentTimeMillis());
        
        // Add any new candidates since last update
        if (description != null && description.getTimestamp() > lastUpdate) {
            response.setLocalDescription(description.getSdp());
        }
        
        // Add any new ICE candidates since last update
        List<WebRTCIceCandidateDto> newCandidates = candidates.stream()
                .filter(candidate -> candidate.getTimestamp() > lastUpdate)
                .toList();
        
        if (!newCandidates.isEmpty()) {
            response.setIceCandidates(newCandidates.stream()
                    .map(WebRTCIceCandidateDto::getCandidate)
                    .toList());
        }
        
        return ResponseEntity.ok(response);
    }
} 