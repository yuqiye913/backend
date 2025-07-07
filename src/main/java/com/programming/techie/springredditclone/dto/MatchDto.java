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
    
    // Match Scores
    private Double overallMatchScore;
    private Double personalityMatchScore;
    private Double interestMatchScore;
    private Double locationMatchScore;
    private Double demographicMatchScore;
    
    // Match Status
    private String matchStatus; // pending, accepted, declined, blocked, expired
    private Instant matchedAt;
    private Instant lastInteractionAt;
    private boolean isRead;
    private Instant readAt;
    
    // Match Preferences
    private String matchType; // friendship, relationship, networking, casual
    private String matchPriority; // low, medium, high, urgent
    private boolean isMutualMatch;
    private boolean isSuperLike;
    private boolean isVerifiedMatch;
    
    // Match Details
    private String matchReason;
    private String commonInterests;
    private String commonHobbies;
    private String commonValues;
    private Integer ageDifference;
    private String locationDistance;
    private String timezoneDifference;
    
    // Communication Preferences
    private String preferredCommunicationMethod;
    private String preferredMeetingType;
    private String availability;
    
    // Match Actions
    private boolean hasSentMessage;
    private boolean hasReceivedMessage;
    private boolean hasCalled;
    private boolean hasReceivedCall;
    private boolean hasMetInPerson;
    
    // Match Feedback
    private Integer userRating;
    private String userFeedback;
    private boolean isReported;
    private String reportReason;
    
    // Match Expiry
    private Instant expiresAt;
    private boolean isExpired;
    
    // Algorithm Data
    private String algorithmVersion;
    private String matchCriteria;
    private Double confidenceScore;
    
    // User Information (for display purposes)
    private String matchedUserUsername;
    private String matchedUserDisplayName;
    private String matchedUserProfilePicture;
    private String matchedUserBio;
    private String matchedUserLocation;
    private Integer matchedUserAge;
    
    // Tags
    private List<String> matchTags;
} 