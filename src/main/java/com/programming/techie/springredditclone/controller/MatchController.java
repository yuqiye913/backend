package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.dto.MatchRequestDto;
import com.programming.techie.springredditclone.service.MatchingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@AllArgsConstructor
@Slf4j
public class MatchController {

    private final MatchingService matchingService;

    /**
     * Find potential matches for the current user
     */
    @GetMapping("/potential")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MatchDto>> findPotentialMatches(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Finding potential matches with limit: {}", limit);
        List<MatchDto> potentialMatches = matchingService.findPotentialMatches(limit);
        return ResponseEntity.ok(potentialMatches);
    }

    /**
     * Create a match between two users
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchDto> createMatch(@Valid @RequestBody MatchRequestDto matchRequest) {
        log.info("Creating match with user: {}", matchRequest.getMatchedUserId());
        MatchDto createdMatch = matchingService.createMatch(matchRequest.getMatchedUserId());
        return ResponseEntity.ok(createdMatch);
    }

    /**
     * Accept a match
     */
    @PostMapping("/{matchId}/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchDto> acceptMatch(@PathVariable Long matchId) {
        log.info("Accepting match: {}", matchId);
        MatchDto acceptedMatch = matchingService.acceptMatch(matchId);
        return ResponseEntity.ok(acceptedMatch);
    }

    /**
     * Decline a match
     */
    @PostMapping("/{matchId}/decline")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> declineMatch(@PathVariable Long matchId) {
        log.info("Declining match: {}", matchId);
        matchingService.declineMatch(matchId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all matches for the current user
     */
    @GetMapping("/my-matches")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MatchDto>> getMyMatches() {
        log.info("Getting matches for current user");
        List<MatchDto> matches = matchingService.getMyMatches();
        return ResponseEntity.ok(matches);
    }

    /**
     * Get mutual matches (where both users accepted)
     */
    @GetMapping("/mutual")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MatchDto>> getMutualMatches() {
        log.info("Getting mutual matches for current user");
        List<MatchDto> mutualMatches = matchingService.getMutualMatches();
        return ResponseEntity.ok(mutualMatches);
    }

    /**
     * Get a specific match by ID
     */
    @GetMapping("/{matchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchDto> getMatchById(@PathVariable Long matchId) {
        log.info("Getting match by ID: {}", matchId);
        MatchDto match = matchingService.getMatchById(matchId);
        return ResponseEntity.ok(match);
    }

    /**
     * Check if two users are matched
     */
    @GetMapping("/check-match")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> areUsersMatched(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        log.info("Checking if users {} and {} are matched", userId1, userId2);
        boolean areMatched = matchingService.areUsersMatched(userId1, userId2);
        return ResponseEntity.ok(areMatched);
    }

    /**
     * Get match statistics for the current user
     */
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MatchingService.MatchStatistics> getMatchStatistics() {
        log.info("Getting match statistics for current user");
        MatchingService.MatchStatistics statistics = matchingService.getMatchStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Rate a match after interaction
     */
    @PostMapping("/{matchId}/rate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> rateMatch(
            @PathVariable Long matchId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String feedback) {
        log.info("Rating match {} with rating: {}", matchId, rating);
        matchingService.rateMatch(matchId, rating, feedback);
        return ResponseEntity.ok().build();
    }

    /**
     * Report a match
     */
    @PostMapping("/{matchId}/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reportMatch(
            @PathVariable Long matchId,
            @RequestParam String reason) {
        log.info("Reporting match {} with reason: {}", matchId, reason);
        matchingService.reportMatch(matchId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * Block a user from future matches
     */
    @PostMapping("/block/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId) {
        log.info("Blocking user: {}", userId);
        matchingService.blockUser(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Unblock a user
     */
    @PostMapping("/unblock/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unblockUser(@PathVariable Long userId) {
        log.info("Unblocking user: {}", userId);
        matchingService.unblockUser(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get blocked users
     */
    @GetMapping("/blocked-users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getBlockedUsers() {
        log.info("Getting blocked users for current user");
        List<Long> blockedUsers = matchingService.getBlockedUsers();
        return ResponseEntity.ok(blockedUsers);
    }
} 