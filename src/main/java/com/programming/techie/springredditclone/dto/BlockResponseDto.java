package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockResponseDto {
    private Long blockId;
    private Long blockerId; // User who is blocking
    private Long blockedUserId; // User who is being blocked
    
    // User information for display
    private String blockerUsername;
    private String blockerDisplayName;
    private String blockerProfilePicture;
    private String blockedUserUsername;
    private String blockedUserDisplayName;
    private String blockedUserProfilePicture;
    
    // Block details
    private String reason;
    private Instant blockedAt;
    private boolean isActive;
    
    // Block statistics
    private Long totalBlocksByUser; // Total blocks by the blocker
    private Long totalBlocksOfUser; // Total blocks of the blocked user
    private boolean isMutualBlock; // Whether both users have blocked each other
    
    // Block history
    private Long previousBlockCount; // Number of times this user was blocked before
    private Instant lastBlockedAt; // When this user was last blocked
    private String blockHistory; // Brief history of previous blocks
    
    // Block impact
    private boolean hasAffectedMatches; // Whether this block affected any matches
    private boolean hasAffectedMessages; // Whether this block affected any messages
    private boolean hasAffectedCalls; // Whether this block affected any calls
    private String impactSummary; // Summary of the block's impact
} 