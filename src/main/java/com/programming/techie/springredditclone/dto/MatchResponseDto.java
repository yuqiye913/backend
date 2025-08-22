package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponseDto {
    
    @NotNull(message = "Match ID is required")
    private Long matchId;
    
    @NotNull(message = "Response action is required")
    private String action; // accept, decline, block
    
    private String responseMessage; // Optional message with response
    private String declineReason; // Reason for declining (if applicable)
} 