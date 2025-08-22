package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.BlockRequestDto;
import com.programming.techie.springredditclone.dto.BlockResponseDto;
import com.programming.techie.springredditclone.dto.BlockListResponseDto;
import com.programming.techie.springredditclone.dto.UnblockRequestDto;

import java.util.List;

public interface BlockService {
    
    /**
     * Block a user
     * @param blockRequest Block request containing user to block and block details
     * @return Block response with block details
     */
    BlockResponseDto blockUser(BlockRequestDto blockRequest);
    
    /**
     * Unblock a user
     * @param unblockRequest Unblock request containing user to unblock
     * @return Block response with unblock details
     */
    BlockResponseDto unblockUser(UnblockRequestDto unblockRequest);
    
    /**
     * Get all users blocked by the current user
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of blocked users with pagination
     */
    BlockListResponseDto getBlockedUsers(int page, int size);
    
    /**
     * Get all users who have blocked the current user
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of users who blocked current user
     */
    BlockListResponseDto getUsersWhoBlockedMe(int page, int size);
    
    /**
     * Get a specific block by ID
     * @param blockId Block ID
     * @return Block details
     */
    BlockResponseDto getBlockById(Long blockId);
    
    /**
     * Check if current user has blocked a specific user
     * @param userId User ID to check
     * @return true if blocked, false otherwise
     */
    boolean hasBlockedUser(Long userId);
    
    /**
     * Check if a specific user has blocked the current user
     * @param userId User ID to check
     * @return true if blocked by user, false otherwise
     */
    boolean isBlockedByUser(Long userId);
    
    /**
     * Check if there is a mutual block between current user and another user
     * @param userId User ID to check
     * @return true if mutual block exists, false otherwise
     */
    boolean isMutualBlock(Long userId);
    
    /**
     * Get block statistics for the current user
     * @return Block statistics
     */
    BlockStatistics getBlockStatistics();
    
    /**
     * Update an existing block
     * @param blockId Block ID to update
     * @param blockRequest Updated block details
     * @return Updated block response
     */
    BlockResponseDto updateBlock(Long blockId, BlockRequestDto blockRequest);
    
    /**
     * Delete a block (permanent)
     * @param blockId Block ID to delete
     */
    void deleteBlock(Long blockId);
    
    /**
     * Get all active blocks for the current user
     * @return List of active blocks
     */
    List<BlockResponseDto> getActiveBlocks();
    
    /**
     * Block statistics class
     */
    class BlockStatistics {
        private final long totalBlocksByUser;
        private final long totalBlocksOfUser;
        private final long activeBlockCount;
        private final long mutualBlockCount;
        private final String mostCommonBlockReason;
        
        public BlockStatistics(long totalBlocksByUser, long totalBlocksOfUser, 
                             long activeBlockCount, long mutualBlockCount,
                             String mostCommonBlockReason) {
            this.totalBlocksByUser = totalBlocksByUser;
            this.totalBlocksOfUser = totalBlocksOfUser;
            this.activeBlockCount = activeBlockCount;
            this.mutualBlockCount = mutualBlockCount;
            this.mostCommonBlockReason = mostCommonBlockReason;
        }
        
        // Getters
        public long getTotalBlocksByUser() { return totalBlocksByUser; }
        public long getTotalBlocksOfUser() { return totalBlocksOfUser; }
        public long getActiveBlockCount() { return activeBlockCount; }
        public long getMutualBlockCount() { return mutualBlockCount; }
        public String getMostCommonBlockReason() { return mostCommonBlockReason; }
    }
} 