package com.programming.techie.springredditclone.service.matching;

import com.programming.techie.springredditclone.dto.MatchDto;
import com.programming.techie.springredditclone.dto.MatchRequestDto;
import com.programming.techie.springredditclone.dto.MatchResponseDto;
import com.programming.techie.springredditclone.dto.MatchStatusDto;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.mapper.MatchMapper;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.MatchRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.BlockService;
import com.programming.techie.springredditclone.service.MatchingService;
import com.programming.techie.springredditclone.service.impl.MatchingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchMapper matchMapper;

    @Mock
    private BlockService blockService;

    private MatchingServiceImpl matchingService;

    private User currentUser;
    private User matchedUser;
    private Match match;
    private MatchDto matchDto;

    @BeforeEach
    void setUp() {
        // Setup test users
        currentUser = new User();
        currentUser.setUserId(1L);
        currentUser.setUsername("testuser1");
        currentUser.setEmail("user1@test.com");
        currentUser.setEnabled(true);

        matchedUser = new User();
        matchedUser.setUserId(2L);
        matchedUser.setUsername("testuser2");
        matchedUser.setEmail("user2@test.com");
        matchedUser.setEnabled(true);

        // Create service with mocked dependencies and test current user
        matchingService = new TestMatchingServiceImpl(matchRepository, userRepository, blockService, matchMapper, currentUser);

        // Setup test match
        match = new Match();
        match.setMatchId(1L);
        match.setUser(currentUser);
        match.setMatchedUser(matchedUser);
        match.setMatchStatus("pending");
        match.setPreferredCommunicationMethod("voice");
        match.setMatchedAt(Instant.now());
        match.setHasCalled(false);
        match.setHasReceivedCall(false);

        // Setup test DTO
        matchDto = new MatchDto();
        matchDto.setMatchId(1L);
        matchDto.setUserId(1L);
        matchDto.setMatchedUserId(2L);
        matchDto.setMatchStatus("pending");
        matchDto.setCallType("voice");
        matchDto.setHasCalled(false);
        matchDto.setHasReceivedCall(false);
    }

    @Test
    void findPotentialMatches_ShouldReturnListOfPotentialMatches() {
        // Arrange
        List<User> potentialUsers = Arrays.asList(matchedUser);
        when(userRepository.findPotentialMatches(anyLong(), anyInt())).thenReturn(potentialUsers);

        // Act
        List<MatchDto> result = matchingService.findPotentialMatches(10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("potential", result.get(0).getMatchStatus());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals(2L, result.get(0).getMatchedUserId());
        assertEquals("testuser2", result.get(0).getMatchedUserUsername());
        verify(userRepository).findPotentialMatches(anyLong(), eq(10));
    }

    @Test
    void createMatch_ShouldCreateNewMatchSuccessfully() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(matchedUser));
        when(matchRepository.findMatchBetweenUsers(currentUser, matchedUser)).thenReturn(Optional.empty());
        when(matchRepository.save(any(Match.class))).thenReturn(match);
        when(matchMapper.mapToDto(match)).thenReturn(matchDto);

        // Act
        MatchDto result = matchingService.createMatch(2L);

        // Assert
        assertNotNull(result);
        assertEquals("pending", result.getMatchStatus());
        assertEquals("voice", result.getCallType());
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void createMatch_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> matchingService.createMatch(999L));
        verify(userRepository).findById(999L);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_ShouldThrowException_WhenMatchAlreadyExists() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(matchedUser));
        when(matchRepository.findMatchBetweenUsers(currentUser, matchedUser)).thenReturn(Optional.of(match));

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> matchingService.createMatch(2L));
        verify(userRepository).findById(2L);
        verify(matchRepository).findMatchBetweenUsers(currentUser, matchedUser);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void acceptMatch_ShouldAcceptMatchSuccessfully() {
        // Arrange
        match.setMatchedUser(currentUser); // Current user is the receiver
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.findMatchBetweenUsers(any(User.class), any(User.class))).thenReturn(Optional.empty());
        when(matchRepository.save(any(Match.class))).thenReturn(match);
        when(matchMapper.mapToDto(match)).thenReturn(matchDto);

        // Act
        MatchDto result = matchingService.acceptMatch(1L);

        // Assert
        assertNotNull(result);
        verify(matchRepository).save(match);
        assertEquals("accepted", match.getMatchStatus());
    }

    @Test
    void acceptMatch_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> matchingService.acceptMatch(999L));
        verify(matchRepository).findById(999L);
    }

    @Test
    void acceptMatch_ShouldThrowException_WhenUserNotReceiver() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> matchingService.acceptMatch(1L));
        verify(matchRepository).findById(1L);
    }

    @Test
    void declineMatch_ShouldDeclineMatchSuccessfully() {
        // Arrange
        match.setMatchedUser(currentUser); // Current user is the receiver
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        matchingService.declineMatch(1L);

        // Assert
        verify(matchRepository).save(match);
        assertEquals("declined", match.getMatchStatus());
    }

    @Test
    void getMyMatches_ShouldReturnUserMatches() {
        // Arrange
        List<Match> matches = Arrays.asList(match);
        when(matchRepository.findByUser(currentUser)).thenReturn(matches);
        when(matchMapper.mapToDto(match)).thenReturn(matchDto);

        // Act
        List<MatchDto> result = matchingService.getMyMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(matchRepository).findByUser(currentUser);
    }

    @Test
    void getMutualMatches_ShouldReturnMutualMatches() {
        // Arrange
        List<Match> mutualMatches = Arrays.asList(match);
        when(matchRepository.findMutualMatchesByUser(currentUser)).thenReturn(mutualMatches);
        when(matchMapper.mapToDto(match)).thenReturn(matchDto);

        // Act
        List<MatchDto> result = matchingService.getMutualMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(matchRepository).findMutualMatchesByUser(currentUser);
    }

    @Test
    void getMatchById_ShouldReturnMatch_WhenUserIsInvolved() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchMapper.mapToDto(match)).thenReturn(matchDto);

        // Act
        MatchDto result = matchingService.getMatchById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getMatchId());
        verify(matchRepository).findById(1L);
    }

    @Test
    void getMatchById_ShouldThrowException_WhenUserNotInvolved() {
        // Arrange
        User otherUser = new User();
        otherUser.setUserId(999L);
        match.setUser(otherUser);
        match.setMatchedUser(otherUser);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> matchingService.getMatchById(1L));
        verify(matchRepository).findById(1L);
    }

    @Test
    void areUsersMatched_ShouldReturnTrue_WhenUsersAreMatched() {
        // Arrange
        User user1 = new User();
        user1.setUserId(1L);
        User user2 = new User();
        user2.setUserId(2L);
        
        Match acceptedMatch = new Match();
        acceptedMatch.setMatchStatus("accepted");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(matchRepository.findMatchBetweenUsers(user1, user2)).thenReturn(Optional.of(acceptedMatch));

        // Act
        boolean result = matchingService.areUsersMatched(1L, 2L);

        // Assert
        assertTrue(result);
    }

    @Test
    void areUsersMatched_ShouldReturnFalse_WhenUsersAreNotMatched() {
        // Arrange
        User user1 = new User();
        user1.setUserId(1L);
        User user2 = new User();
        user2.setUserId(2L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(matchRepository.findMatchBetweenUsers(user1, user2)).thenReturn(Optional.empty());

        // Act
        boolean result = matchingService.areUsersMatched(1L, 2L);

        // Assert
        assertFalse(result);
    }

    @Test
    void rateMatch_ShouldRateMatchSuccessfully() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        matchingService.rateMatch(1L, 5, "Great call!");

        // Assert
        verify(matchRepository).save(match);
        assertEquals(5, match.getUserRating());
        assertEquals("Great call!", match.getUserFeedback());
    }

    @Test
    void rateMatch_ShouldThrowException_WhenRatingInvalid() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> matchingService.rateMatch(1L, 6, "Invalid rating"));
        assertThrows(SpringRedditException.class, () -> matchingService.rateMatch(1L, 0, "Invalid rating"));
        verify(matchRepository, times(2)).findById(1L);
    }

    @Test
    void reportMatch_ShouldReportMatchSuccessfully() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // Act
        matchingService.reportMatch(1L, "Inappropriate behavior");

        // Assert
        verify(matchRepository).save(match);
        assertTrue(match.isReported());
        assertEquals("Inappropriate behavior", match.getReportReason());
    }

    @Test
    void blockUser_ShouldBlockUserSuccessfully() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(matchedUser));

        // Act
        matchingService.blockUser(2L);

        // Assert
        // Verify the method was called (actual blocking logic would be implemented)
        verify(userRepository).findById(2L);
    }

    @Test
    void blockUser_ShouldThrowException_WhenBlockingSelf() {
        // Act & Assert
        assertThrows(SpringRedditException.class, () -> matchingService.blockUser(1L));
    }
} 