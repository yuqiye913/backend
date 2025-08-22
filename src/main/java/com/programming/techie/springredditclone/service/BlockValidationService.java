package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.model.User;

/**
 * Utility service for validating block relationships between users.
 * This service provides common block validation methods that can be used
 * across all other services to ensure consistent block checking.
 */
public interface BlockValidationService {
    
    /**
     * Check if there's any block relationship between two users
     * @param user1 First user
     * @param user2 Second user
     * @return true if either user has blocked the other, false otherwise
     */
    boolean hasBlockRelationship(User user1, User user2);
    
    /**
     * Check if there's any block relationship between current user and target user
     * @param targetUserId ID of the target user
     * @return true if either user has blocked the other, false otherwise
     */
    boolean hasBlockRelationship(Long targetUserId);
    
    /**
     * Validate that current user can interact with target user
     * @param targetUserId ID of the target user
     * @throws SpringRedditException if there's a block relationship
     */
    void validateCanInteract(Long targetUserId);
    
    /**
     * Validate that current user can interact with target user
     * @param targetUser Target user
     * @throws SpringRedditException if there's a block relationship
     */
    void validateCanInteract(User targetUser);
    
    /**
     * Validate that current user can view content from target user
     * @param targetUserId ID of the target user
     * @throws SpringRedditException if there's a block relationship
     */
    void validateCanViewContent(Long targetUserId);
    
    /**
     * Validate that current user can view content from target user
     * @param targetUser Target user
     * @throws SpringRedditException if there's a block relationship
     */
    void validateCanViewContent(User targetUser);
    
    /**
     * Check if current user has blocked target user
     * @param targetUserId ID of the target user
     * @return true if current user has blocked target user
     */
    boolean hasBlockedUser(Long targetUserId);
    
    /**
     * Check if current user has been blocked by target user
     * @param targetUserId ID of the target user
     * @return true if current user has been blocked by target user
     */
    boolean isBlockedByUser(Long targetUserId);
    
    /**
     * Check if there's a mutual block between current user and target user
     * @param targetUserId ID of the target user
     * @return true if there's a mutual block
     */
    boolean isMutualBlock(Long targetUserId);
    
    /**
     * Filter out blocked users from a list of user IDs
     * @param userIds List of user IDs to filter
     * @return List of user IDs that don't have block relationships with current user
     */
    java.util.List<Long> filterBlockedUsers(java.util.List<Long> userIds);
    
    /**
     * Get all user IDs that current user has blocked
     * @return List of blocked user IDs
     */
    java.util.List<Long> getBlockedUserIds();
    
    /**
     * Get all user IDs that have blocked current user
     * @return List of user IDs that have blocked current user
     */
    java.util.List<Long> getUsersWhoBlockedMe();
} 