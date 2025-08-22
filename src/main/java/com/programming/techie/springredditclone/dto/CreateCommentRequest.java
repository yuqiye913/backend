package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentRequest {
    @NotNull(message = "Post ID is required")
    private Long postId;
    
    @NotBlank(message = "Comment text is required")
    private String text;
    
    // Optional: for replies to other comments
    private Long parentCommentId;
} 