package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallingRequestDto {
    private Long requestId;
    
    @NotNull(message = "Caller ID is required")
    private Long callerId;
    
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
    
    @NotNull(message = "Call type is required")
    private String callType; // voice, video
    
    private String callPurpose; // casual, business, emergency, etc.
    private Instant requestedAt;
    private String status; // pending, accepted, declined, missed, cancelled
    private String priority; // low, normal, high, urgent
    private boolean isScheduled;
    private Instant scheduledTime;
    private String scheduledDuration; // Expected duration in minutes
    private String notes; // Additional notes about the call
    private boolean isRead;
    private Instant readAt;
    private boolean requireConfirmation; // Whether receiver needs to confirm before call
    private String meetingLink; // For video calls
    private String meetingPassword; // For video calls
} 