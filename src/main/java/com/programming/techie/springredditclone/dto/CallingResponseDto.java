package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallingResponseDto {
    private Long responseId;
    
    @NotNull(message = "Request ID is required")
    private Long requestId;
    
    @NotNull(message = "Responder ID is required")
    private Long responderId;
    
    @NotNull(message = "Response status is required")
    private String responseStatus; // accepted, declined, rescheduled, blocked
    
    private String responseMessage; // Optional message with the response
    private Instant respondedAt;
    private Instant proposedTime; // If rescheduling, propose new time
    private String proposedDuration; // If rescheduling, propose new duration
    private String declineReason; // Reason for declining
    private boolean isRead;
    private Instant readAt;
    private boolean autoAcceptFutureCalls; // Whether to auto-accept future calls from this user
    private String preferredCallType; // Preferred call type for future calls
    private String preferredCallTime; // Preferred time for future calls
    private boolean requireAdvanceNotice; // Whether advance notice is required for future calls
    private String advanceNoticePeriod; // How much advance notice is required (e.g., "2 hours")
} 