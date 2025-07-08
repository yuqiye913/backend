package com.programming.techie.springredditclone.service;

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
     * Get all posts
     * @return List of post responses
     */
    List<PostResponse> getAllPosts();
    
    /**
     * Get posts by subreddit ID
     * @param subredditId Subreddit ID
     * @return List of post responses
     */
    List<PostResponse> getPostsBySubreddit(Long subredditId);
    
    /**
     * Get posts by username
     * @param username Username
     * @return List of post responses
     */
    List<PostResponse> getPostsByUsername(String username);
    
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
}
