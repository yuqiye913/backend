package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private Long postId;
    
    // Multiple subreddits only - cleaner API
    @NotEmpty(message = "At least one subreddit must be specified")
    private List<String> subredditNames;
    
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;
    private String url;
    
    @NotBlank(message = "Post content/description cannot be empty")
    private String description;
}
