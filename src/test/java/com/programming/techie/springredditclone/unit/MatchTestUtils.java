package com.programming.techie.springredditclone.unit;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.dto.MatchRequestDto;
import com.programming.techie.springredditclone.dto.MatchResponseDto;
import com.programming.techie.springredditclone.dto.MatchStatusDto;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for creating test data for match-related tests
 */
public class MatchTestUtils {

    /**
     * Create a test user with basic information
     */
    public static User createTestUser(Long userId, String username, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setEnabled(true);
        user.setCreated(Instant.now());
        return user;
    }

    /**
     * Create a test match with basic information
     */
    public static Match createTestMatch(Long matchId, User user, User matchedUser, String status) {
        Match match = new Match();
        match.setMatchId(matchId);
        match.setUser(user);
        match.setMatchedUser(matchedUser);
        match.setMatchStatus(status);
        match.setPreferredCommunicationMethod("voice");
        match.setAvailability("evening");
        match.setMatchedAt(Instant.now());
        match.setLastInteractionAt(Instant.now());
        match.setRead(false);
        match.setHasCalled(false);
        match.setHasReceivedCall(false);
        match.setUserRating(null);
        match.setUserFeedback(null);
        match.setReported(false);
        match.setExpiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        match.setExpired(false);
        match.setMatchTags(Arrays.asList("voice", "casual"));
        return match;
    }

    /**
     * Create a test MatchDto with basic information
     */
    public static MatchDto createTestMatchDto(Long matchId, Long userId, Long matchedUserId, String status) {
        MatchDto matchDto = new MatchDto();
        matchDto.setMatchId(matchId);
        matchDto.setUserId(userId);
        matchDto.setMatchedUserId(matchedUserId);
        matchDto.setMatchStatus(status);
        matchDto.setCallType("voice");
        matchDto.setCallPurpose("casual");
        matchDto.setPreferredCallTime("evening");
        matchDto.setAutoAcceptCalls(false);
        matchDto.setRequireAdvanceNotice(false);
        matchDto.setAdvanceNoticePeriod("30 minutes");
        matchDto.setHasCalled(false);
        matchDto.setHasReceivedCall(false);
        matchDto.setLastCallAt(null);
        matchDto.setLastCallDuration(null);
        matchDto.setLastCallStatus(null);
        matchDto.setTotalCalls(0L);
        matchDto.setSuccessfulCalls(0L);
        matchDto.setMissedCalls(0L);
        matchDto.setDeclinedCalls(0L);
        matchDto.setTotalCallDuration(0L);
        matchDto.setConnectionQuality("good");
        matchDto.setNetworkType("wifi");
        matchDto.setDeviceType("mobile");
        matchDto.setMatchedUserUsername("testuser" + matchedUserId);
        matchDto.setMatchedUserDisplayName("Test User " + matchedUserId);
        matchDto.setMatchedUserProfilePicture("profile" + matchedUserId + ".jpg");
        matchDto.setMatchedUserBio("Test bio for user " + matchedUserId);
        matchDto.setMatchedUserLocation("Test City");
        matchDto.setMatchedUserAge(25);
        matchDto.setUserRating(null);
        matchDto.setUserFeedback(null);
        matchDto.setReported(false);
        matchDto.setReportReason(null);
        matchDto.setExpiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        matchDto.setExpired(false);
        matchDto.setMatchTags(Arrays.asList("voice", "casual"));
        return matchDto;
    }

    /**
     * Create a test MatchRequestDto
     */
    public static MatchRequestDto createTestMatchRequestDto(Long matchedUserId) {
        MatchRequestDto requestDto = new MatchRequestDto();
        requestDto.setMatchedUserId(matchedUserId);
        requestDto.setCallType("voice");
        requestDto.setCallPurpose("casual");
        requestDto.setPreferredCallTime("evening");
        requestDto.setAutoAcceptCalls(false);
        requestDto.setRequireAdvanceNotice(false);
        requestDto.setAdvanceNoticePeriod("30 minutes");
        requestDto.setMatchMessage("Hi, would you like to have a voice call?");
        return requestDto;
    }

    /**
     * Create a test MatchResponseDto
     */
    public static MatchResponseDto createTestMatchResponseDto(Long matchId, String action) {
        MatchResponseDto responseDto = new MatchResponseDto();
        responseDto.setMatchId(matchId);
        responseDto.setAction(action);
        responseDto.setResponseMessage("Response message");
        responseDto.setDeclineReason(action.equals("decline") ? "Not interested" : null);
        return responseDto;
    }

    /**
     * Create a test MatchStatusDto
     */
    public static MatchStatusDto createTestMatchStatusDto(Long userId, String username) {
        MatchStatusDto statusDto = new MatchStatusDto();
        statusDto.setUserId(userId);
        statusDto.setUsername(username);
        statusDto.setTotalMatches(5L);
        statusDto.setPendingMatches(2L);
        statusDto.setAcceptedMatches(2L);
        statusDto.setDeclinedMatches(1L);
        statusDto.setMutualMatches(1L);
        statusDto.setTotalCalls(10L);
        statusDto.setSuccessfulCalls(8L);
        statusDto.setMissedCalls(1L);
        statusDto.setDeclinedCalls(1L);
        statusDto.setTotalCallDuration(3600L); // 1 hour in seconds
        statusDto.setLastCallAt(Instant.now().minusSeconds(3600));
        statusDto.setLastCallStatus("completed");
        statusDto.setLastMatchAt(Instant.now().minusSeconds(86400));
        statusDto.setLastInteractionAt(Instant.now().minusSeconds(3600));
        statusDto.setPreferredCallType("voice");
        statusDto.setPreferredCallTime("evening");
        statusDto.setAutoAcceptCalls(false);
        statusDto.setRequireAdvanceNotice(false);
        statusDto.setAdvanceNoticePeriod("30 minutes");
        statusDto.setActive(true);
        statusDto.setAvailability("available");
        return statusDto;
    }

    /**
     * Create a list of test users
     */
    public static List<User> createTestUsers() {
        return Arrays.asList(
            createTestUser(1L, "testuser1", "user1@test.com"),
            createTestUser(2L, "testuser2", "user2@test.com"),
            createTestUser(3L, "testuser3", "user3@test.com"),
            createTestUser(4L, "testuser4", "user4@test.com")
        );
    }

    /**
     * Create a list of test matches
     */
    public static List<Match> createTestMatches(User user1, User user2, User user3) {
        return Arrays.asList(
            createTestMatch(1L, user1, user2, "pending"),
            createTestMatch(2L, user1, user3, "accepted"),
            createTestMatch(3L, user2, user1, "declined")
        );
    }

    /**
     * Create a list of test MatchDtos
     */
    public static List<MatchDto> createTestMatchDtos() {
        return Arrays.asList(
            createTestMatchDto(1L, 1L, 2L, "pending"),
            createTestMatchDto(2L, 1L, 3L, "accepted"),
            createTestMatchDto(3L, 2L, 1L, "declined")
        );
    }

    /**
     * Update match with call history
     */
    public static Match updateMatchWithCallHistory(Match match, boolean hasCalled, boolean hasReceivedCall) {
        match.setHasCalled(hasCalled);
        match.setHasReceivedCall(hasReceivedCall);
        match.setLastInteractionAt(Instant.now());
        return match;
    }

    /**
     * Update match with rating and feedback
     */
    public static Match updateMatchWithRating(Match match, Integer rating, String feedback) {
        match.setUserRating(rating);
        match.setUserFeedback(feedback);
        match.setLastInteractionAt(Instant.now());
        return match;
    }

    /**
     * Update match status
     */
    public static Match updateMatchStatus(Match match, String status) {
        match.setMatchStatus(status);
        match.setLastInteractionAt(Instant.now());
        return match;
    }
} 