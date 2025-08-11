package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.MatchingControlDto;
import com.programming.techie.springredditclone.service.RandomVideoCallService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

    private final RandomVideoCallService randomVideoCallService;

    /**
     * Enable the random video call matching system
     */
    @PostMapping("/matching/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchingControlDto> enableMatching() {
        log.info("Admin requested to ENABLE random video call matching");
        randomVideoCallService.enableMatching();
        
        MatchingControlDto response = new MatchingControlDto(
            true, 
            "enable", 
            "Random video call matching has been ENABLED successfully"
        );
        
        // Populate additional status information
        RandomVideoCallService.MatchingSystemStatus status = randomVideoCallService.getMatchingSystemStatus();
        response.setTotalUsersInQueue(status.getTotalUsersInQueue());
        response.setPriorityUsersInQueue(status.getPriorityUsersInQueue());
        response.setAverageWaitTime(status.getAverageWaitTime());
        response.setLastMatchTime(status.getLastMatchTime());
        response.setMatchesToday(status.getMatchesToday());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Disable the random video call matching system
     */
    @PostMapping("/matching/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchingControlDto> disableMatching() {
        log.info("Admin requested to DISABLE random video call matching");
        randomVideoCallService.disableMatching();
        
        MatchingControlDto response = new MatchingControlDto(
            false, 
            "disable", 
            "Random video call matching has been DISABLED successfully"
        );
        
        // Populate additional status information
        RandomVideoCallService.MatchingSystemStatus status = randomVideoCallService.getMatchingSystemStatus();
        response.setTotalUsersInQueue(status.getTotalUsersInQueue());
        response.setPriorityUsersInQueue(status.getPriorityUsersInQueue());
        response.setAverageWaitTime(status.getAverageWaitTime());
        response.setLastMatchTime(status.getLastMatchTime());
        response.setMatchesToday(status.getMatchesToday());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if matching is currently enabled
     */
    @GetMapping("/matching/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchingControlDto> getMatchingStatus() {
        boolean enabled = randomVideoCallService.isMatchingEnabled();
        log.info("Admin checked matching status: {}", enabled);
        
        MatchingControlDto response = new MatchingControlDto(
            enabled, 
            "status", 
            enabled ? "Matching system is currently ENABLED" : "Matching system is currently DISABLED"
        );
        
        // Populate additional status information
        RandomVideoCallService.MatchingSystemStatus status = randomVideoCallService.getMatchingSystemStatus();
        response.setTotalUsersInQueue(status.getTotalUsersInQueue());
        response.setPriorityUsersInQueue(status.getPriorityUsersInQueue());
        response.setAverageWaitTime(status.getAverageWaitTime());
        response.setLastMatchTime(status.getLastMatchTime());
        response.setMatchesToday(status.getMatchesToday());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get detailed matching system status
     */
    @GetMapping("/matching/system-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RandomVideoCallService.MatchingSystemStatus> getMatchingSystemStatus() {
        RandomVideoCallService.MatchingSystemStatus status = randomVideoCallService.getMatchingSystemStatus();
        log.info("Admin requested matching system status: {}", status.getStatus());
        return ResponseEntity.ok(status);
    }

    /**
     * Toggle the matching system (enable if disabled, disable if enabled)
     */
    @PostMapping("/matching/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchingControlDto> toggleMatching() {
        boolean currentStatus = randomVideoCallService.isMatchingEnabled();
        String action = currentStatus ? "disable" : "enable";
        String message = currentStatus ? 
            "Random video call matching has been DISABLED" : 
            "Random video call matching has been ENABLED";
        
        if (currentStatus) {
            randomVideoCallService.disableMatching();
            log.info("Admin toggled matching system: DISABLED");
        } else {
            randomVideoCallService.enableMatching();
            log.info("Admin toggled matching system: ENABLED");
        }
        
        MatchingControlDto response = new MatchingControlDto(!currentStatus, "toggle", message);
        
        // Populate additional status information
        RandomVideoCallService.MatchingSystemStatus status = randomVideoCallService.getMatchingSystemStatus();
        response.setTotalUsersInQueue(status.getTotalUsersInQueue());
        response.setPriorityUsersInQueue(status.getPriorityUsersInQueue());
        response.setAverageWaitTime(status.getAverageWaitTime());
        response.setLastMatchTime(status.getLastMatchTime());
        response.setMatchesToday(status.getMatchesToday());
        
        return ResponseEntity.ok(response);
    }
} 