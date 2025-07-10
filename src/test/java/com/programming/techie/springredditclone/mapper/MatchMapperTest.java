package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.mapper.impl.MatchMapperImpl;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchMapperTest {

    private MatchMapper matchMapper;
    private User user1;
    private User user2;
    private Match match;

    @BeforeEach
    void setUp() {
        matchMapper = new MatchMapperImpl();
        
        // Setup test users
        user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("testuser1");
        user1.setEmail("user1@test.com");
        
        user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("testuser2");
        user2.setEmail("user2@test.com");
        
        // Setup test match
        match = new Match();
        match.setMatchId(1L);
        match.setUser(user1);
        match.setMatchedUser(user2);
        match.setMatchStatus("pending");
        match.setPreferredCommunicationMethod("voice");
        match.setAvailability("evening");
        match.setMatchedAt(Instant.now());
        match.setLastInteractionAt(Instant.now());
        match.setRead(false);
        match.setHasCalled(false);
        match.setHasReceivedCall(false);
        match.setUserRating(5);
        match.setUserFeedback("Great call!");
        match.setReported(false);
        match.setExpiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        match.setExpired(false);
        match.setMatchTags(Arrays.asList("voice", "casual"));
    }

    @Test
    void mapToDto_ShouldMapAllFieldsCorrectly() {
        // Act
        MatchDto result = matchMapper.mapToDto(match);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getMatchId());
        assertEquals(1L, result.getUserId());
        assertEquals(2L, result.getMatchedUserId());
        assertEquals("pending", result.getMatchStatus());
        assertEquals("voice", result.getCallType());
        assertEquals("evening", result.getPreferredCallTime());
        assertEquals("testuser2", result.getMatchedUserUsername());
        assertFalse(result.isHasCalled());
        assertFalse(result.isHasReceivedCall());
        assertEquals(5, result.getUserRating());
        assertEquals("Great call!", result.getUserFeedback());
        assertFalse(result.isReported());
        assertFalse(result.isExpired());
        assertEquals(Arrays.asList("voice", "casual"), result.getMatchTags());
    }

    @Test
    void mapToDto_ShouldHandleNullValues() {
        // Arrange
        match.setUserRating(null);
        match.setUserFeedback(null);
        match.setMatchTags(null);

        // Act
        MatchDto result = matchMapper.mapToDto(match);

        // Assert
        assertNotNull(result);
        assertNull(result.getUserRating());
        assertNull(result.getUserFeedback());
        assertNull(result.getMatchTags());
    }

    @Test
    void mapToDto_ShouldReturnNull_WhenInputIsNull() {
        // Act
        MatchDto result = matchMapper.mapToDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    void mapToEntity_ShouldMapAllFieldsCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setMatchId(1L);
        matchDto.setUserId(1L);
        matchDto.setMatchedUserId(2L);
        matchDto.setMatchStatus("accepted");
        matchDto.setCallType("voice");
        matchDto.setPreferredCallTime("morning");
        matchDto.setHasCalled(true);
        matchDto.setHasReceivedCall(true);
        matchDto.setUserRating(4);
        matchDto.setUserFeedback("Good call");
        matchDto.setReported(false);
        matchDto.setExpired(false);
        matchDto.setMatchTags(Arrays.asList("voice", "business"));

        // Act
        Match result = matchMapper.mapToEntity(matchDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getMatchId());
        assertEquals("accepted", result.getMatchStatus());
        assertEquals("voice", result.getPreferredCommunicationMethod());
        assertEquals("morning", result.getAvailability());
        assertTrue(result.isHasCalled());
        assertTrue(result.isHasReceivedCall());
        assertEquals(4, result.getUserRating());
        assertEquals("Good call", result.getUserFeedback());
        assertFalse(result.isReported());
        assertFalse(result.isExpired());
        assertEquals(Arrays.asList("voice", "business"), result.getMatchTags());
    }

    @Test
    void mapToEntity_ShouldHandleNullValues() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setMatchId(1L);
        matchDto.setUserId(1L);
        matchDto.setMatchedUserId(2L);
        matchDto.setUserRating(null);
        matchDto.setUserFeedback(null);
        matchDto.setMatchTags(null);

        // Act
        Match result = matchMapper.mapToEntity(matchDto);

        // Assert
        assertNotNull(result);
        assertNull(result.getUserRating());
        assertNull(result.getUserFeedback());
        assertNull(result.getMatchTags());
    }

    @Test
    void mapToEntity_ShouldReturnNull_WhenInputIsNull() {
        // Act
        Match result = matchMapper.mapToEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    void mapToDto_ShouldMapCallHistoryFields() {
        // Arrange
        match.setHasCalled(true);
        match.setHasReceivedCall(true);

        // Act
        MatchDto result = matchMapper.mapToDto(match);

        // Assert
        assertTrue(result.isHasCalled());
        assertTrue(result.isHasReceivedCall());
    }

    @Test
    void mapToDto_ShouldMapConnectionQualityDefaults() {
        // Act
        MatchDto result = matchMapper.mapToDto(match);

        // Assert
        assertEquals("good", result.getConnectionQuality());
        assertEquals("wifi", result.getNetworkType());
        assertEquals("mobile", result.getDeviceType());
    }

    @Test
    void mapToDto_ShouldMapCallPreferencesDefaults() {
        // Act
        MatchDto result = matchMapper.mapToDto(match);

        // Assert
        assertEquals("casual", result.getCallPurpose());
        assertFalse(result.isAutoAcceptCalls());
        assertFalse(result.isRequireAdvanceNotice());
        assertEquals("30 minutes", result.getAdvanceNoticePeriod());
    }
} 