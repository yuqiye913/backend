package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowRequestDto {
    @NotNull(message = "FollowingId must not be null")
    private Long followingId;  // The user to be followed (follower is the authenticated user)
    // Optionally, add a message or timestamp
} 