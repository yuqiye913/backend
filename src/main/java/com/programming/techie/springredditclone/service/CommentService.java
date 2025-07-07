package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.CommentsDto;

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
}
