package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.VoteStatusDto;

public interface VoteService {
    
    /**
     * Like a post
     * @param postId Post ID to like
     */
    void likePost(Long postId);
    
    /**
     * Unlike a post
     * @param postId Post ID to unlike
     */
    void unlikePost(Long postId);
    
    /**
     * Get current like status for a post
     * @param postId Post ID
     * @return Like status including whether user has liked and total count
     */
    VoteStatusDto getPostLikeStatus(Long postId);
    
    /**
     * Like a comment
     * @param commentId Comment ID to like
     */
    void likeComment(Long commentId);
    
    /**
     * Unlike a comment
     * @param commentId Comment ID to unlike
     */
    void unlikeComment(Long commentId);
    
    /**
     * Get current like status for a comment
     * @param commentId Comment ID
     * @return Like status including whether user has liked and total count
     */
    VoteStatusDto getCommentLikeStatus(Long commentId);
}
