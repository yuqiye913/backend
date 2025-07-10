package com.programming.techie.springredditclone.dto;

import com.programming.techie.springredditclone.model.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentVoteRequest {
    @NotNull(message = "Comment ID is required")
    private Long commentId;
    
    @NotNull(message = "Vote type is required")
    private VoteType voteType;
} 