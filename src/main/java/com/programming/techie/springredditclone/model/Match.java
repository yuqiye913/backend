package com.programming.techie.springredditclone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user; // The user who is being matched
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_user_id", referencedColumnName = "userId")
    private User matchedUser; // The user who is matched with
    
    // Match Criteria and Scores
    private Double overallMatchScore; // Overall compatibility score (0-100)
    private Double personalityMatchScore; // Personality compatibility score
    private Double interestMatchScore; // Interest compatibility score
    private Double locationMatchScore; // Location proximity score
    private Double demographicMatchScore; // Demographic compatibility score
    
    // Match Status
    private String matchStatus; // pending, accepted, declined, blocked, expired
    private Instant matchedAt;
    private Instant lastInteractionAt;
    private boolean isRead;
    private Instant readAt;
    
    // Match Preferences
    private String matchType; // friendship, relationship, networking, casual
    private String matchPriority; // low, medium, high, urgent
    private boolean isMutualMatch; // Whether both users matched each other
    private boolean isSuperLike; // Whether this is a super like match
    private boolean isVerifiedMatch; // Whether this is a verified user match
    
    // Match Details
    private String matchReason; // Why these users were matched
    private String commonInterests; // Comma-separated common interests
    private String commonHobbies; // Comma-separated common hobbies
    private String commonValues; // Comma-separated common values
    private Integer ageDifference; // Age difference between users
    private String locationDistance; // Distance between users
    private String timezoneDifference; // Timezone difference
    
    // Communication Preferences
    private String preferredCommunicationMethod; // text, voice, video, in-person
    private String preferredMeetingType; // coffee, dinner, activity, virtual
    private String availability; // weekday evenings, weekends, flexible
    
    // Match Actions
    private boolean hasSentMessage;
    private boolean hasReceivedMessage;
    private boolean hasCalled;
    private boolean hasReceivedCall;
    private boolean hasMetInPerson;
    
    // Match Feedback
    private Integer userRating; // Rating given by user (1-5)
    private String userFeedback; // Feedback given by user
    private boolean isReported;
    private String reportReason;
    
    // Match Expiry
    private Instant expiresAt;
    private boolean isExpired;
    
    // Match Algorithm Data
    private String algorithmVersion; // Version of matching algorithm used
    private String matchCriteria; // JSON string of criteria used for matching
    private Double confidenceScore; // Algorithm confidence in this match
    
    @ElementCollection
    @CollectionTable(name = "match_tags", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "tag")
    private List<String> matchTags; // Tags for categorizing matches
    
    @PrePersist
    protected void onCreate() {
        matchedAt = Instant.now();
        lastInteractionAt = Instant.now();
        matchStatus = "pending";
        isRead = false;
        isMutualMatch = false;
        isSuperLike = false;
        isVerifiedMatch = false;
        hasSentMessage = false;
        hasReceivedMessage = false;
        hasCalled = false;
        hasReceivedCall = false;
        hasMetInPerson = false;
        isReported = false;
        isExpired = false;
        expiresAt = Instant.now().plusSeconds(7 * 24 * 60 * 60); // 7 days expiry
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastInteractionAt = Instant.now();
        if (expiresAt != null && Instant.now().isAfter(expiresAt)) {
            isExpired = true;
            matchStatus = "expired";
        }
    }
} 