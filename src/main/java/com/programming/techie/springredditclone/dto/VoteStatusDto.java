package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteStatusDto {
    private boolean isLiked; // true if user has liked, false otherwise
    private Integer likeCount; // total like count for the post/comment
} 