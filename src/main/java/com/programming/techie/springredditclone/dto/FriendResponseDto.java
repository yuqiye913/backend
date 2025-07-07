package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponseDto {
    private Long responseId;
    
    @NotNull(message = "Request ID is required")
    private Long requestId;
    
    @NotNull(message = "Responder ID is required")
    private Long responderId;
    
    @NotNull(message = "Response status is required")
    private String responseStatus; // accepted, declined, blocked
    
    private String responseMessage; // Optional message with the response
    private Instant respondedAt;
    private String relationshipType; // friend, acquaintance, colleague, etc.
    private boolean isRead;
    private Instant readAt;
    private boolean autoFollow; // Whether to automatically follow after accepting
    private boolean allowDirectMessages; // Whether to allow DMs from this user
    private boolean allowPhoneCalls; // Whether to allow calls from this user
} 