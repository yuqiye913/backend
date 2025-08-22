package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockRequestDto {
    
    @NotNull(message = "Blocked user ID is required")
    private Long blockedUserId; // The user to be blocked
    
    private String reason; // Optional reason for blocking
} 