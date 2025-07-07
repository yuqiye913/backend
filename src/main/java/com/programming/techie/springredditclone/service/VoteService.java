package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.VoteDto;

public interface VoteService {
    
    /**
     * Vote on a post (upvote or downvote)
     * @param voteDto Vote data containing post ID and vote type
     */
    void vote(VoteDto voteDto);
}
