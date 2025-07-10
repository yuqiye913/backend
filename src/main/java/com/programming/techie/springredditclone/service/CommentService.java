package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.CommentsDto;
import com.programming.techie.springredditclone.dto.CursorPageResponse;

import java.util.List;

public interface CommentService {
    
    /**
     * Save a new comment
     * @param commentsDto Comment data to save
     */
    void save(CommentsDto commentsDto);
    
    /**
     * Get all comments for a specific post
     * @param postId Post ID
     * @return List of comment DTOs
     */
    List<CommentsDto> getAllCommentsForPost(Long postId);
    
    /**
     * Get paginated comments for a specific post using cursor-based pagination
     * @param postId Post ID
     * @param cursor Cursor for pagination (can be null for first page)
     * @param limit Number of comments to return
     * @return Paginated response with comments and next cursor
     */
    CursorPageResponse<CommentsDto> getCommentsForPost(Long postId, String cursor, Integer limit);
    
    /**
     * Get paginated comments by a specific user using cursor-based pagination
     * @param userName Username
     * @param cursor Cursor for pagination (can be null for first page)
     * @param limit Number of comments to return
     * @return Paginated response with comments and next cursor
     */
    CursorPageResponse<CommentsDto> getCommentsForUser(String userName, String cursor, Integer limit);
    
    /**
     * Get paginated replies for a specific comment using cursor-based pagination
     * @param commentId Comment ID
     * @param cursor Cursor for pagination (can be null for first page)
     * @param limit Number of replies to return
     * @return Paginated response with replies and next cursor
     */
    CursorPageResponse<CommentsDto> getRepliesForComment(Long commentId, String cursor, Integer limit);
    
    /**
     * Get all comments by a specific user
     * @param userName Username
     * @return List of comment DTOs
     */
    List<CommentsDto> getAllCommentsForUser(String userName);
    
    /**
     * Check if comment contains inappropriate language
     * @param comment Comment text to check
     * @return true if contains swear words, false otherwise
     */
    boolean containsSwearWords(String comment);
    
    /**
     * Delete a comment (soft delete)
     * @param commentId Comment ID to delete
     */
    void deleteComment(Long commentId);
}
