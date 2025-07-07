package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.SubredditDto;

import java.util.List;

public interface SubredditService {
    
    /**
     * Save a new subreddit
     * @param subredditDto Subreddit data to save
     * @return Saved subreddit DTO
     */
    SubredditDto save(SubredditDto subredditDto);
    
    /**
     * Get all subreddits
     * @return List of subreddit DTOs
     */
    List<SubredditDto> getAll();
    
    /**
     * Get a subreddit by ID
     * @param id Subreddit ID
     * @return Subreddit DTO
     */
    SubredditDto getSubreddit(Long id);
}
