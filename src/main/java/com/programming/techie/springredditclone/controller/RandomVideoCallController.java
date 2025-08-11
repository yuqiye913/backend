package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.RandomVideoCallRequestDto;
import com.programming.techie.springredditclone.dto.RandomVideoCallResponseDto;
import com.programming.techie.springredditclone.service.RandomVideoCallService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/random-video-calls")
@AllArgsConstructor
public class RandomVideoCallController {

    private final RandomVideoCallService randomVideoCallService;

    /**
     * Request a random video call - join the queue
     */
    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RandomVideoCallResponseDto> requestRandomVideoCall(@Valid @RequestBody RandomVideoCallRequestDto request) {
        RandomVideoCallResponseDto response = randomVideoCallService.requestRandomVideoCall(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Check queue status for a request
     */
    @GetMapping("/status/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RandomVideoCallResponseDto> checkQueueStatus(@PathVariable String requestId) {
        RandomVideoCallResponseDto response = randomVideoCallService.checkQueueStatus(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a random video call request
     */
    @DeleteMapping("/cancel/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelRandomVideoCall(@PathVariable String requestId) {
        randomVideoCallService.cancelRandomVideoCall(requestId);
        return ResponseEntity.ok().build();
    }

    /**
     * Accept a matched video call
     */
    @PostMapping("/accept/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RandomVideoCallResponseDto> acceptMatchedCall(@PathVariable String requestId) {
        RandomVideoCallResponseDto response = randomVideoCallService.acceptMatchedCall(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Decline a matched video call
     */
    @PostMapping("/decline/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> declineMatchedCall(
            @PathVariable String requestId,
            @RequestParam(required = false) String reason) {
        randomVideoCallService.declineMatchedCall(requestId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * End an active random video call
     */
    @PostMapping("/end/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RandomVideoCallResponseDto> endRandomVideoCall(@PathVariable String requestId) {
        RandomVideoCallResponseDto response = randomVideoCallService.endRandomVideoCall(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get queue statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RandomVideoCallService.QueueStatistics> getQueueStatistics() {
        RandomVideoCallService.QueueStatistics statistics = randomVideoCallService.getQueueStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get recent random video calls for a user
     */
    @GetMapping("/recent/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RandomVideoCallResponseDto>> getRecentRandomVideoCalls(@PathVariable Long userId) {
        List<RandomVideoCallResponseDto> calls = randomVideoCallService.getRecentRandomVideoCalls(userId);
        return ResponseEntity.ok(calls);
    }

    /**
     * Get current user's active random video call request
     */
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RandomVideoCallResponseDto> getCurrentUserActiveRequest() {
        RandomVideoCallResponseDto response = randomVideoCallService.getCurrentUserActiveRequest();
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Update preferences for a request
     */
    @PutMapping("/preferences/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RandomVideoCallResponseDto> updatePreferences(
            @PathVariable String requestId,
            @Valid @RequestBody RandomVideoCallRequestDto preferences) {
        RandomVideoCallResponseDto response = randomVideoCallService.updatePreferences(requestId, preferences);
        return ResponseEntity.ok(response);
    }

    /**
     * Report a user from random video call
     */
    @PostMapping("/report/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reportUser(
            @PathVariable String requestId,
            @RequestParam String reason) {
        randomVideoCallService.reportUser(requestId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * Block a user from random video calls
     */
    @PostMapping("/block/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> blockUserFromRandomCalls(@PathVariable Long userId) {
        randomVideoCallService.blockUserFromRandomCalls(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Unblock a user from random video calls
     */
    @DeleteMapping("/block/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unblockUserFromRandomCalls(@PathVariable Long userId) {
        randomVideoCallService.unblockUserFromRandomCalls(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get blocked users for random video calls
     */
    @GetMapping("/blocked-users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getBlockedUsersForRandomCalls() {
        List<Long> blockedUsers = randomVideoCallService.getBlockedUsersForRandomCalls();
        return ResponseEntity.ok(blockedUsers);
    }
    
    /**
     * Enable the matching system
     */
    @PostMapping("/admin/matching/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableMatching() {
        randomVideoCallService.enableMatching();
        return ResponseEntity.ok().build();
    }
    
    /**
     * Disable the matching system
     */
    @PostMapping("/admin/matching/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableMatching() {
        randomVideoCallService.disableMatching();
        return ResponseEntity.ok().build();
    }
    
    /**
     * Check if matching is enabled
     */
    @GetMapping("/admin/matching/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> isMatchingEnabled() {
        boolean enabled = randomVideoCallService.isMatchingEnabled();
        return ResponseEntity.ok(enabled);
    }
    
    /**
     * Get detailed matching system status
     */
    @GetMapping("/admin/matching/system-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RandomVideoCallService.MatchingSystemStatus> getMatchingSystemStatus() {
        RandomVideoCallService.MatchingSystemStatus status = randomVideoCallService.getMatchingSystemStatus();
        return ResponseEntity.ok(status);
    }
} 