package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetIntroDto {
    private Long userId;
    private String username;
    private String displayName;
    private String bio;
    private String tagline;
    private String profilePictureUrl;
    private String aboutMe;
} 