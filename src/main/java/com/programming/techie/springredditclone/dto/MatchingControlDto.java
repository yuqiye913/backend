package com.programming.techie.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingControlDto {
    
    private boolean matchingEnabled;
    private String action; // "enable", "disable", "toggle", "status"
    private String message;
    private Instant timestamp;
    private Long totalUsersInQueue;
    private Long priorityUsersInQueue;
    private Long averageWaitTime;
    private Long lastMatchTime;
    private Long matchesToday;
    
    public MatchingControlDto(boolean matchingEnabled, String action, String message) {
        this.matchingEnabled = matchingEnabled;
        this.action = action;
        this.message = message;
        this.timestamp = Instant.now();
    }
} 