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
    
    // Match-related information
    private Long matchId; // Associated match ID if this response is for a match call
    private boolean isMatchResponse; // Whether this response is for a match call
    private String matchType; // friendship, relationship, networking, casual
    private Double matchScore; // Overall match score between users
    
    // Connection and availability information
    private String responderNetworkType; // wifi, cellular, ethernet
    private String responderDeviceType; // mobile, desktop, tablet
    private String responderLocation; // Responder's current location
    private String responderStatus; // available, busy, offline, in-call
    private String responderTimezone; // Responder's timezone
    
    // Response preferences and settings
    private boolean blockFutureCalls; // Whether to block future calls from this user
    private String blockReason; // Reason for blocking future calls
    private boolean allowScheduledCalls; // Whether to allow scheduled calls
    private boolean allowEmergencyCalls; // Whether to allow emergency calls
    private String preferredCommunicationMethod; // text, voice, video, in-person
    
    // User information for display
    private String responderUsername;
    private String responderDisplayName;
    private String responderProfilePicture;
    private String requesterUsername;
    private String requesterDisplayName;
    private String requesterProfilePicture;
    
    // Call history context
    private Long previousCallsCount; // Number of previous calls between users
    private Instant lastCallAt; // When the last call was made
    private String lastCallStatus; // Status of the last call
    private Long lastCallDuration; // Duration of last call in seconds
    
    // Response quality and feedback
    private String responseQuality; // excellent, good, fair, poor
    private String responseNotes; // Additional notes about the response
    private boolean isUrgentResponse; // Whether this is an urgent response
    private String responseCategory; // personal, professional, emergency, social
    
    // Future call preferences
    private String futureCallPreferences; // JSON string of future call preferences
    private boolean enableCallNotifications; // Whether to enable call notifications
    private String notificationPreferences; // JSON string of notification preferences
} 