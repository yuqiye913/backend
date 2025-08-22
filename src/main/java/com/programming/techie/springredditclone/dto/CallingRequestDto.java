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
    
    // Match-related information
    private Long matchId; // Associated match ID if this call is between matched users
    private boolean isMatchCall; // Whether this call is between matched users
    private String matchType; // friendship, relationship, networking, casual
    private Double matchScore; // Overall match score between users
    
    // Connection and network information
    private String callerNetworkType; // wifi, cellular, ethernet
    private String receiverNetworkType; // wifi, cellular, ethernet
    private String callerDeviceType; // mobile, desktop, tablet
    private String receiverDeviceType; // mobile, desktop, tablet
    private String callerLocation; // Caller's location for proximity
    private String receiverLocation; // Receiver's location for proximity
    private String timezoneDifference; // Timezone difference between users
    
    // Call preferences and settings
    private boolean autoAccept; // Whether receiver should auto-accept this call
    private String preferredCallTime; // Preferred time for the call
    private boolean requireAdvanceNotice; // Whether advance notice is required
    private String advanceNoticePeriod; // How much advance notice is required
    
    // User information for display
    private String callerUsername;
    private String callerDisplayName;
    private String callerProfilePicture;
    private String receiverUsername;
    private String receiverDisplayName;
    private String receiverProfilePicture;
    
    // Call history context
    private Long previousCallsCount; // Number of previous calls between users
    private Instant lastCallAt; // When the last call was made
    private String lastCallStatus; // Status of the last call
    private Long lastCallDuration; // Duration of last call in seconds
    
    // Call quality expectations
    private String expectedCallQuality; // excellent, good, fair, poor
    private boolean isHighPriorityCall; // Whether this is a high priority call
    private String callCategory; // personal, professional, emergency, social
} 