package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {
    private Long matchId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Matched user ID is required")
    private Long matchedUserId;
    
    // Match Status
    private String matchStatus; // pending, accepted, declined, blocked, expired
    private Instant matchedAt;
    private Instant lastInteractionAt;
    private boolean isRead;
    private Instant readAt;
    
    // Voice Calling Preferences
    private String callType; // voice, video
    private String callPurpose; // casual, business, emergency, social
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls; // Whether to auto-accept calls from this match
    private boolean requireAdvanceNotice; // Whether advance notice is required
    private String advanceNoticePeriod; // How much advance notice (e.g., "30 minutes")
    
    // Call History
    private boolean hasCalled; // Whether user has called this match
    private boolean hasReceivedCall; // Whether user has received call from this match
    private Instant lastCallAt; // When the last call was made
    private Long lastCallDuration; // Duration of last call in seconds
    private String lastCallStatus; // completed, missed, declined
    private Long totalCalls; // Total number of calls between users
    private Long successfulCalls; // Number of successful calls
    private Long missedCalls; // Number of missed calls
    private Long declinedCalls; // Number of declined calls
    private Long totalCallDuration; // Total call duration in seconds
    
    // Connection Quality
    private String connectionQuality; // excellent, good, fair, poor
    private String networkType; // wifi, cellular, ethernet
    private String deviceType; // mobile, desktop, tablet
    
    // User Information (for display purposes)
    private String matchedUserUsername;
    private String matchedUserDisplayName;
    private String matchedUserProfilePicture;
    private String matchedUserBio;
    private String matchedUserLocation;
    private Integer matchedUserAge;
    
    // Match Feedback
    private Integer userRating; // Rating given by user (1-5)
    private String userFeedback; // Feedback given by user
    private boolean isReported;
    private String reportReason;
    
    // Match Expiry
    private Instant expiresAt;
    private boolean isExpired;
    
    // Tags
    private List<String> matchTags;
} 