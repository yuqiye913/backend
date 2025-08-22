package com.programming.techie.springredditclone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequestDto {
    
    @NotNull(message = "User ID to match with is required")
    private Long matchedUserId;
    
    // Voice Calling Preferences
    private String callType; // voice, video
    private String callPurpose; // casual, business, emergency, social
    private String preferredCallTime; // morning, afternoon, evening, anytime
    private boolean autoAcceptCalls; // Whether to auto-accept calls
    private boolean requireAdvanceNotice; // Whether advance notice is required
    private String advanceNoticePeriod; // How much advance notice (e.g., "30 minutes")
    
    private String matchMessage; // Optional message when requesting match
} 