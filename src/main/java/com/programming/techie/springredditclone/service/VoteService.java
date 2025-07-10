package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.CommentVoteRequest;
import com.programming.techie.springredditclone.dto.VoteDto;

public interface VoteService {
    
    /**
     * Vote on a post (upvote or downvote)
     * @param voteDto Vote data containing post ID and vote type
     */
    void vote(VoteDto voteDto);
    
    /**
     * Vote on a comment (upvote or downvote)
     * @param commentVoteRequest Vote data containing comment ID and vote type
     */
    void voteOnComment(CommentVoteRequest commentVoteRequest);
    
    /**
     * Get vote count for a comment
     * @param commentId Comment ID
     * @return Vote count (positive for upvotes, negative for downvotes)
     */
    Integer getCommentVoteCount(Long commentId);
}
