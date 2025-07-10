package com.programming.techie.springredditclone.mapper.impl;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.mapper.MatchMapper;
import com.programming.techie.springredditclone.model.Match;
import org.springframework.stereotype.Component;

@Component
public class MatchMapperImpl implements MatchMapper {

    @Override
    public MatchDto mapToDto(Match match) {
        if (match == null) {
            return null;
        }

        MatchDto matchDto = new MatchDto();
        matchDto.setMatchId(match.getMatchId());
        matchDto.setUserId(match.getUser().getUserId());
        matchDto.setMatchedUserId(match.getMatchedUser().getUserId());
        
        // Match Status
        matchDto.setMatchStatus(match.getMatchStatus());
        matchDto.setMatchedAt(match.getMatchedAt());
        matchDto.setLastInteractionAt(match.getLastInteractionAt());
        matchDto.setRead(match.isRead());
        matchDto.setReadAt(match.getReadAt());
        
        // Voice Calling Preferences (using existing fields)
        matchDto.setCallType(match.getPreferredCommunicationMethod()); // Map to existing field
        matchDto.setCallPurpose("casual"); // Default value
        matchDto.setPreferredCallTime(match.getAvailability()); // Map to existing field
        matchDto.setAutoAcceptCalls(false); // Default value
        matchDto.setRequireAdvanceNotice(false); // Default value
        matchDto.setAdvanceNoticePeriod("30 minutes"); // Default value
        
        // Call History (using existing fields)
        matchDto.setHasCalled(match.isHasCalled());
        matchDto.setHasReceivedCall(match.isHasReceivedCall());
        matchDto.setLastCallAt(null); // Will be populated from call history
        matchDto.setLastCallDuration(null); // Will be populated from call history
        matchDto.setLastCallStatus(null); // Will be populated from call history
        matchDto.setTotalCalls(0L); // Will be calculated from call history
        matchDto.setSuccessfulCalls(0L); // Will be calculated from call history
        matchDto.setMissedCalls(0L); // Will be calculated from call history
        matchDto.setDeclinedCalls(0L); // Will be calculated from call history
        matchDto.setTotalCallDuration(0L); // Will be calculated from call history
        
        // Connection Quality (default values)
        matchDto.setConnectionQuality("good"); // Default value
        matchDto.setNetworkType("wifi"); // Default value
        matchDto.setDeviceType("mobile"); // Default value
        
        // Match Feedback
        matchDto.setUserRating(match.getUserRating());
        matchDto.setUserFeedback(match.getUserFeedback());
        matchDto.setReported(match.isReported());
        matchDto.setReportReason(match.getReportReason());
        
        // Match Expiry
        matchDto.setExpiresAt(match.getExpiresAt());
        matchDto.setExpired(match.isExpired());
        
        // User Information
        if (match.getMatchedUser() != null) {
            matchDto.setMatchedUserUsername(match.getMatchedUser().getUsername());
            // Add other user fields as needed
        }
        
        // Tags
        matchDto.setMatchTags(match.getMatchTags());
        
        return matchDto;
    }

    @Override
    public Match mapToEntity(MatchDto matchDto) {
        if (matchDto == null) {
            return null;
        }

        Match match = new Match();
        match.setMatchId(matchDto.getMatchId());
        
        // Note: User and matchedUser should be set by the service layer
        // as they require database lookups
        
        // Match Status
        match.setMatchStatus(matchDto.getMatchStatus());
        match.setMatchedAt(matchDto.getMatchedAt());
        match.setLastInteractionAt(matchDto.getLastInteractionAt());
        match.setRead(matchDto.isRead());
        match.setReadAt(matchDto.getReadAt());
        
        // Voice Calling Preferences (map to existing fields)
        match.setPreferredCommunicationMethod(matchDto.getCallType());
        match.setAvailability(matchDto.getPreferredCallTime());
        
        // Match Actions (keep existing calling fields)
        match.setHasCalled(matchDto.isHasCalled());
        match.setHasReceivedCall(matchDto.isHasReceivedCall());
        
        // Match Feedback
        match.setUserRating(matchDto.getUserRating());
        match.setUserFeedback(matchDto.getUserFeedback());
        match.setReported(matchDto.isReported());
        match.setReportReason(matchDto.getReportReason());
        
        // Match Expiry
        match.setExpiresAt(matchDto.getExpiresAt());
        match.setExpired(matchDto.isExpired());
        
        // Tags
        match.setMatchTags(matchDto.getMatchTags());
        
        return match;
    }
} 