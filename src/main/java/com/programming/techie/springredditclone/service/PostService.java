package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.CursorPageResponse;
import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;

import java.util.List;

public interface PostService {
    
    /**
     * Save a new post
     * @param postRequest Post data to save
     */
    void save(PostRequest postRequest);
    
    /**
     * Get a post by ID
     * @param id Post ID
     * @return Post response
     */
    PostResponse getPost(Long id);
    
    /**
     * Get all posts with cursor-based pagination
     * @param cursor Cursor for pagination (optional)
     * @param limit Number of posts to return
     * @return Cursor page response
     */
    CursorPageResponse<PostResponse> getAllPosts(String cursor, int limit);
    
    /**
     * Get posts by subreddit ID with cursor-based pagination
     * @param subredditId Subreddit ID
     * @param cursor Cursor for pagination (optional)
     * @param limit Number of posts to return
     * @return Cursor page response
     */
    CursorPageResponse<PostResponse> getPostsBySubreddit(Long subredditId, String cursor, int limit);
    
    /**
     * Get posts by username with cursor-based pagination
     * @param username Username
     * @param cursor Cursor for pagination (optional)
     * @param limit Number of posts to return
     * @return Cursor page response
     */
    CursorPageResponse<PostResponse> getPostsByUsername(String username, String cursor, int limit);
    
    /**
     * Get posts that belong to any of the specified subreddits
     * @param subredditNames List of subreddit names
     * @return List of post responses
     */
    List<PostResponse> getPostsByMultipleSubreddits(List<String> subredditNames);
    
    /**
     * Update an existing post
     * @param postId Post ID to update
     * @param postRequest Updated post data
     */
    void updatePost(Long postId, PostRequest postRequest);
    
    /**
     * Delete a post by ID
     * @param postId Post ID to delete
     */
    void deletePost(Long postId);
    
    /**
     * Search posts by subreddit name
     * @param subredditName Subreddit name to search for
     * @return List of post responses
     */
    List<PostResponse> searchPostsBySubreddit(String subredditName);
    
    /**
     * Search posts by subreddit name and/or post name
     * @param searchTerm Search term to match against subreddit name or post name
     * @return List of post responses
     */
    List<PostResponse> searchPosts(String searchTerm);
    
    /**
     * Get promoted posts based on promotion algorithm
     * @param limit Number of promoted posts to return
     * @return List of promoted post responses
     */
    List<PostResponse> getPromotedPosts(int limit);
    
    /**
     * Get promoted posts based on promotion algorithm with cursor-based pagination
     * @param cursor Cursor for pagination (optional)
     * @param limit Number of promoted posts to return
     * @return Cursor page response
     */
    CursorPageResponse<PostResponse> getPromotedPosts(String cursor, int limit);
}
