package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFollowersDto {
    private Long userId;
    private String username;
    private String email;
    private Instant created;
    private boolean enabled;
    private Instant followedAt;
    private boolean isActive;
    private boolean isMuted;
    private boolean isCloseFriend;
}
