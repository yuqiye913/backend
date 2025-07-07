package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private Long requestId;
    
    @NotNull(message = "Sender ID is required")
    private Long senderId;
    
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
    
    private String message; // Optional message with the friend request
    private Instant requestedAt;
    private String status; // pending, accepted, declined, cancelled
    private String requestType; // friend, acquaintance, colleague, etc.
    private boolean isRead;
    private Instant readAt;
} 