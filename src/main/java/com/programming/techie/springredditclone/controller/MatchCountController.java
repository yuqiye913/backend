package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.MatchCountDto;
import com.programming.techie.springredditclone.service.MatchingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/match-counts")
@AllArgsConstructor
@Slf4j
public class MatchCountController {

    private final MatchingService matchingService;

    /**
     * Get detailed match count for the current user
     * @param matchCountDto Match count criteria
     * @return Detailed match count information
     */
    @PostMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getMatchCount(@Valid @RequestBody MatchCountDto matchCountDto) {
        log.info("Getting match count for user: {}", matchCountDto.getUserId());
        MatchCountDto result = matchingService.getMatchCount(matchCountDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Get total match count for the current user
     * @return Total match count
     */
    @GetMapping("/total")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getTotalMatchCount() {
        log.info("Getting total match count for current user");
        MatchCountDto request = new MatchCountDto();
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get match count by status for the current user
     * @param status Match status (pending, accepted, declined, etc.)
     * @return Match count for the specified status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getMatchCountByStatus(@PathVariable String status) {
        log.info("Getting match count by status: {} for current user", status);
        MatchCountDto request = new MatchCountDto();
        request.setMatchStatus(status);
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get mutual match count for the current user
     * @return Mutual match count
     */
    @GetMapping("/mutual")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getMutualMatchCount() {
        log.info("Getting mutual match count for current user");
        MatchCountDto request = new MatchCountDto();
        request.setIsMutualMatch(true);
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get pending match count for the current user
     * @return Pending match count
     */
    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getPendingMatchCount() {
        log.info("Getting pending match count for current user");
        MatchCountDto request = new MatchCountDto();
        request.setMatchStatus("pending");
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get accepted match count for the current user
     * @return Accepted match count
     */
    @GetMapping("/accepted")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getAcceptedMatchCount() {
        log.info("Getting accepted match count for current user");
        MatchCountDto request = new MatchCountDto();
        request.setMatchStatus("accepted");
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get recent match count (last 7 days) for the current user
     * @return Recent match count
     */
    @GetMapping("/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getRecentMatchCount() {
        log.info("Getting recent match count for current user");
        MatchCountDto request = new MatchCountDto();
        // TODO: Set current user ID from security context
        // TODO: Set time filters for last 7 days
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get high-quality match count (score >= 80) for the current user
     * @return High-quality match count
     */
    @GetMapping("/high-quality")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getHighQualityMatchCount() {
        log.info("Getting high-quality match count for current user");
        MatchCountDto request = new MatchCountDto();
        request.setMinMatchScore(80.0);
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get match count with communication activity for the current user
     * @return Match count with communication
     */
    @GetMapping("/with-communication")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getMatchCountWithCommunication() {
        log.info("Getting match count with communication for current user");
        MatchCountDto request = new MatchCountDto();
        request.setHasSentMessage(true);
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Get video call enabled match count for the current user
     * @return Video call enabled match count
     */
    @GetMapping("/video-enabled")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchCountDto> getVideoEnabledMatchCount() {
        log.info("Getting video-enabled match count for current user");
        MatchCountDto request = new MatchCountDto();
        request.setPreferredCommunicationMethod("video");
        // TODO: Set current user ID from security context
        MatchCountDto result = matchingService.getMatchCount(request);
        return ResponseEntity.ok(result);
    }
} 