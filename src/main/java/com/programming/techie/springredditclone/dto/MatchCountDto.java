package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchCountDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    // Match Status Filters
    private String matchStatus; // pending, accepted, declined, blocked, expired, all
    private List<String> matchStatuses; // Multiple statuses to count
    
    // Match Type Filters
    private String matchType; // friendship, relationship, networking, casual, all
    private List<String> matchTypes; // Multiple types to count
    
    // Time-based Filters
    private Instant createdAfter; // Count matches created after this time
    private Instant createdBefore; // Count matches created before this time
    private Instant lastInteractionAfter; // Count matches with interaction after this time
    private Instant lastInteractionBefore; // Count matches with interaction before this time
    
    // Match Quality Filters
    private Double minMatchScore; // Minimum overall match score
    private Double maxMatchScore; // Maximum overall match score
    private Double minPersonalityScore; // Minimum personality match score
    private Double minInterestScore; // Minimum interest match score
    private Double minLocationScore; // Minimum location match score
    
    // Match Characteristics
    private Boolean isMutualMatch; // true for mutual matches only, false for non-mutual, null for all
    private Boolean isSuperLike; // true for super likes only, false for regular, null for all
    private Boolean isVerifiedMatch; // true for verified matches only, false for non-verified, null for all
    private Boolean isRead; // true for read matches, false for unread, null for all
    private Boolean isExpired; // true for expired matches, false for active, null for all
    
    // Communication Filters
    private String preferredCommunicationMethod; // text, voice, video, in-person, all
    private Boolean hasSentMessage; // true for matches with sent messages, false for none, null for all
    private Boolean hasReceivedMessage; // true for matches with received messages, false for none, null for all
    private Boolean hasCalled; // true for matches with calls made, false for none, null for all
    private Boolean hasReceivedCall; // true for matches with received calls, false for none, null for all
    
    // Location Filters
    private String locationDistance; // nearby, same-city, same-country, international, all
    private Integer maxAgeDifference; // Maximum age difference between users
    private String timezoneDifference; // Specific timezone difference or range
    
    // Tag Filters
    private List<String> includeTags; // Tags that must be present
    private List<String> excludeTags; // Tags that must not be present
    
    // Pagination and Limits
    private Integer limit; // Maximum number of matches to count (optional)
    private Boolean includeExpired; // Whether to include expired matches in count
    
    // Response Fields (for when this DTO is used as response)
    private Long totalMatches;
    private Long pendingMatches;
    private Long acceptedMatches;
    private Long declinedMatches;
    private Long mutualMatches;
    private Long superLikeMatches;
    private Long verifiedMatches;
    private Long unreadMatches;
    private Long expiredMatches;
    private Long activeMatches;
    
    // Communication Counts
    private Long matchesWithMessages;
    private Long matchesWithCalls;
    private Long matchesWithVideoCalls;
    
    // Quality Distribution
    private Long highQualityMatches; // Matches with score >= 80
    private Long mediumQualityMatches; // Matches with score 50-79
    private Long lowQualityMatches; // Matches with score < 50
    
    // Time-based Counts
    private Long recentMatches; // Matches created in last 7 days
    private Long recentlyActiveMatches; // Matches with interaction in last 30 days
    
    // Additional Metadata
    private Instant countGeneratedAt;
    private String countReason; // Why this count was requested
    private Boolean isRealTime; // Whether this is a real-time count or cached
    
    // Constructor for simple count requests
    public MatchCountDto(Long userId) {
        this.userId = userId;
        this.matchStatus = "all";
        this.matchType = "all";
        this.includeExpired = false;
    }
    
    // Constructor for status-specific count
    public MatchCountDto(Long userId, String matchStatus) {
        this.userId = userId;
        this.matchStatus = matchStatus;
        this.matchType = "all";
        this.includeExpired = false;
    }
    
    // Constructor for type-specific count
    public MatchCountDto(Long userId, String matchStatus, String matchType) {
        this.userId = userId;
        this.matchStatus = matchStatus;
        this.matchType = matchType;
        this.includeExpired = false;
    }
} 