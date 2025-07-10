package com.programming.techie.springredditclone.repository.matching;

import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.MatchRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MatchRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private Match match1;
    private Match match2;

    @BeforeEach
    void setUp() {
        // Create test users
        user1 = new User();
        user1.setUsername("testuser1");
        user1.setEmail("user1@test.com");
        user1.setPassword("password");
        user1.setEnabled(true);
        user1.setCreated(Instant.now());
        user1 = entityManager.persistAndFlush(user1);

        user2 = new User();
        user2.setUsername("testuser2");
        user2.setEmail("user2@test.com");
        user2.setPassword("password");
        user2.setEnabled(true);
        user2.setCreated(Instant.now());
        user2 = entityManager.persistAndFlush(user2);

        user3 = new User();
        user3.setUsername("testuser3");
        user3.setEmail("user3@test.com");
        user3.setPassword("password");
        user3.setEnabled(true);
        user3.setCreated(Instant.now());
        user3 = entityManager.persistAndFlush(user3);

        // Create test matches
        match1 = new Match();
        match1.setUser(user1);
        match1.setMatchedUser(user2);
        match1.setMatchStatus("pending");
        match1.setPreferredCommunicationMethod("voice");
        match1.setMatchedAt(Instant.now());
        match1.setHasCalled(false);
        match1.setHasReceivedCall(false);
        match1 = entityManager.persistAndFlush(match1);

        match2 = new Match();
        match2.setUser(user1);
        match2.setMatchedUser(user3);
        match2.setMatchStatus("accepted");
        match2.setPreferredCommunicationMethod("voice");
        match2.setMatchedAt(Instant.now());
        match2.setHasCalled(true);
        match2.setHasReceivedCall(true);
        match2 = entityManager.persistAndFlush(match2);

        entityManager.clear();
    }

    @Test
    void findByUser_ShouldReturnAllMatchesForUser() {
        // Act
        List<Match> matches = matchRepository.findByUser(user1);

        // Assert
        assertEquals(2, matches.size());
        assertTrue(matches.stream().allMatch(match -> match.getUser().equals(user1)));
    }

    @Test
    void findByMatchedUser_ShouldReturnAllMatchesWhereUserIsMatched() {
        // Act
        List<Match> matches = matchRepository.findByMatchedUser(user2);

        // Assert
        assertEquals(1, matches.size());
        assertEquals(user2, matches.get(0).getMatchedUser());
    }

    @Test
    void findByMatchStatus_ShouldReturnMatchesWithSpecificStatus() {
        // Act
        List<Match> pendingMatches = matchRepository.findByMatchStatus("pending");
        List<Match> acceptedMatches = matchRepository.findByMatchStatus("accepted");

        // Assert
        assertEquals(1, pendingMatches.size());
        assertEquals("pending", pendingMatches.get(0).getMatchStatus());
        
        assertEquals(1, acceptedMatches.size());
        assertEquals("accepted", acceptedMatches.get(0).getMatchStatus());
    }

    @Test
    void findMatchBetweenUsers_ShouldReturnMatch_WhenMatchExists() {
        // Act
        Optional<Match> result = matchRepository.findMatchBetweenUsers(user1, user2);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user1, result.get().getUser());
        assertEquals(user2, result.get().getMatchedUser());
    }

    @Test
    void findMatchBetweenUsers_ShouldReturnEmpty_WhenNoMatchExists() {
        // Act
        Optional<Match> result = matchRepository.findMatchBetweenUsers(user2, user3);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findMatchBetweenUsers_ShouldReturnMatch_WhenReversedOrder() {
        // Act
        Optional<Match> result = matchRepository.findMatchBetweenUsers(user2, user1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user1, result.get().getUser());
        assertEquals(user2, result.get().getMatchedUser());
    }

    @Test
    void findMatchesByUserAndStatus_ShouldReturnMatchesWithStatus() {
        // Act
        List<Match> pendingMatches = matchRepository.findMatchesByUserAndStatus(user1, "pending");
        List<Match> acceptedMatches = matchRepository.findMatchesByUserAndStatus(user1, "accepted");

        // Assert
        assertEquals(1, pendingMatches.size());
        assertEquals("pending", pendingMatches.get(0).getMatchStatus());
        
        assertEquals(1, acceptedMatches.size());
        assertEquals("accepted", acceptedMatches.get(0).getMatchStatus());
    }

    @Test
    void findUnreadMatchesByUser_ShouldReturnUnreadMatches() {
        // Arrange
        match1.setRead(false);
        match2.setRead(true);
        entityManager.persistAndFlush(match1);
        entityManager.persistAndFlush(match2);

        // Act
        List<Match> unreadMatches = matchRepository.findUnreadMatchesByUser(user1);

        // Assert
        assertEquals(1, unreadMatches.size());
        assertFalse(unreadMatches.get(0).isRead());
    }

    @Test
    void findMutualMatchesByUser_ShouldReturnMutualMatches() {
        // Arrange
        match1.setMutualMatch(true);
        match2.setMutualMatch(false);
        entityManager.persistAndFlush(match1);
        entityManager.persistAndFlush(match2);

        // Act
        List<Match> mutualMatches = matchRepository.findMutualMatchesByUser(user1);

        // Assert
        assertEquals(1, mutualMatches.size());
        assertTrue(mutualMatches.get(0).isMutualMatch());
    }

    @Test
    void countPendingMatchesByUser_ShouldReturnCorrectCount() {
        // Act
        Long count = matchRepository.countPendingMatchesByUser(user1);

        // Assert
        assertEquals(1L, count);
    }

    @Test
    void countUnreadMatchesByUser_ShouldReturnCorrectCount() {
        // Arrange
        match1.setRead(false);
        match2.setRead(false);
        entityManager.persistAndFlush(match1);
        entityManager.persistAndFlush(match2);

        // Act
        Long count = matchRepository.countUnreadMatchesByUser(user1);

        // Assert
        assertEquals(2L, count);
    }

    @Test
    void countMutualMatchesByUser_ShouldReturnCorrectCount() {
        // Arrange
        match1.setMutualMatch(true);
        match2.setMutualMatch(true);
        entityManager.persistAndFlush(match1);
        entityManager.persistAndFlush(match2);

        // Act
        Long count = matchRepository.countMutualMatchesByUser(user1);

        // Assert
        assertEquals(2L, count);
    }

    @Test
    void findByUserOrderByScore_ShouldReturnMatchesOrderedByScore() {
        // Arrange
        match1.setOverallMatchScore(80.0);
        match2.setOverallMatchScore(95.0);
        entityManager.persistAndFlush(match1);
        entityManager.persistAndFlush(match2);

        // Act
        List<Match> matches = matchRepository.findMatchesByUserOrderByScore(user1);

        // Assert
        assertEquals(2, matches.size());
        assertEquals(95.0, matches.get(0).getOverallMatchScore());
        assertEquals(80.0, matches.get(1).getOverallMatchScore());
    }

    @Test
    void findActiveMatchesByUser_ShouldReturnNonExpiredMatches() {
        // Arrange
        match1.setExpired(false);
        match2.setExpired(true);
        entityManager.persistAndFlush(match1);
        entityManager.persistAndFlush(match2);

        // Act
        List<Match> activeMatches = matchRepository.findActiveMatchesByUser(user1);

        // Assert
        assertEquals(1, activeMatches.size());
        assertFalse(activeMatches.get(0).isExpired());
    }

    @Test
    void findMatchesWithCommunicationHistory_ShouldReturnMatchesWithCalls() {
        // Act
        List<Match> matchesWithHistory = matchRepository.findMatchesWithCommunicationHistory(user1);

        // Assert
        assertEquals(1, matchesWithHistory.size());
        assertTrue(matchesWithHistory.get(0).isHasCalled() || matchesWithHistory.get(0).isHasReceivedCall());
    }
} 