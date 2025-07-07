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
}
