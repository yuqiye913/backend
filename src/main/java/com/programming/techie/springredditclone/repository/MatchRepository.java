package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    // Basic queries
    List<Match> findByUser(User user);
    List<Match> findByMatchedUser(User matchedUser);
    List<Match> findByMatchStatus(String matchStatus);
    List<Match> findByMatchType(String matchType);
    List<Match> findByIsMutualMatch(boolean isMutualMatch);
    @Query("SELECT m FROM Match m WHERE m.isSuperLike = :isSuperLike")
    List<Match> findByIsSuperLike(@Param("isSuperLike") boolean isSuperLike);
    List<Match> findByIsVerifiedMatch(boolean isVerifiedMatch);
    
    // Find matches for a specific user
    @Query("SELECT m FROM Match m WHERE m.user = :user ORDER BY m.overallMatchScore DESC")
    List<Match> findMatchesByUserOrderByScore(@Param("user") User user);
    
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.matchStatus = :status ORDER BY m.matchedAt DESC")
    List<Match> findMatchesByUserAndStatus(@Param("user") User user, @Param("status") String status);
    
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.isRead = false ORDER BY m.matchedAt DESC")
    List<Match> findUnreadMatchesByUser(@Param("user") User user);
    
    // Find mutual matches
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.isMutualMatch = true ORDER BY m.matchedAt DESC")
    List<Match> findMutualMatchesByUser(@Param("user") User user);
    
    // Find high-scoring matches
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.overallMatchScore >= :minScore ORDER BY m.overallMatchScore DESC")
    List<Match> findHighScoringMatches(@Param("user") User user, @Param("minScore") Double minScore);
    
    // Find matches by location
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.locationMatchScore >= :minLocationScore ORDER BY m.locationMatchScore DESC")
    List<Match> findMatchesByLocationScore(@Param("user") User user, @Param("minLocationScore") Double minLocationScore);
    
    // Find matches by interest
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.interestMatchScore >= :minInterestScore ORDER BY m.interestMatchScore DESC")
    List<Match> findMatchesByInterestScore(@Param("user") User user, @Param("minInterestScore") Double minInterestScore);
    
    // Find matches by personality
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.personalityMatchScore >= :minPersonalityScore ORDER BY m.personalityMatchScore DESC")
    List<Match> findMatchesByPersonalityScore(@Param("user") User user, @Param("minPersonalityScore") Double minPersonalityScore);
    
    // Find active matches (not expired)
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.isExpired = false ORDER BY m.lastInteractionAt DESC")
    List<Match> findActiveMatchesByUser(@Param("user") User user);
    
    // Find matches that need attention (high priority, unread, etc.)
    @Query("SELECT m FROM Match m WHERE m.user = :user AND (m.matchPriority = 'high' OR m.matchPriority = 'urgent') AND m.isRead = false ORDER BY m.matchPriority DESC, m.matchedAt DESC")
    List<Match> findPriorityMatchesByUser(@Param("user") User user);
    
    // Find matches with recent interactions
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.lastInteractionAt >= :since ORDER BY m.lastInteractionAt DESC")
    List<Match> findRecentMatchesByUser(@Param("user") User user, @Param("since") Instant since);
    
    // Find matches by age difference
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.ageDifference <= :maxAgeDifference ORDER BY m.ageDifference ASC")
    List<Match> findMatchesByAgeDifference(@Param("user") User user, @Param("maxAgeDifference") Integer maxAgeDifference);
    
    // Find matches with specific tags
    @Query("SELECT m FROM Match m WHERE m.user = :user AND :tag MEMBER OF m.matchTags ORDER BY m.overallMatchScore DESC")
    List<Match> findMatchesByTag(@Param("user") User user, @Param("tag") String tag);
    
    // Count queries
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND m.matchStatus = 'pending'")
    Long countPendingMatchesByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND m.isRead = false")
    Long countUnreadMatchesByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND m.isMutualMatch = true")
    Long countMutualMatchesByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND m.overallMatchScore >= :minScore")
    Long countHighScoringMatchesByUser(@Param("user") User user, @Param("minScore") Double minScore);
    
    // Check if two users are matched
    @Query("SELECT m FROM Match m WHERE (m.user = :user1 AND m.matchedUser = :user2) OR (m.user = :user2 AND m.matchedUser = :user1)")
    Optional<Match> findMatchBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    // Find expired matches
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.isExpired = true ORDER BY m.expiresAt DESC")
    List<Match> findExpiredMatchesByUser(@Param("user") User user);
    
    // Find matches that expire soon
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.expiresAt <= :expiryTime AND m.isExpired = false ORDER BY m.expiresAt ASC")
    List<Match> findMatchesExpiringSoon(@Param("user") User user, @Param("expiryTime") Instant expiryTime);
    
    // Pagination support
    Page<Match> findByUser(User user, Pageable pageable);
    Page<Match> findByUserAndMatchStatus(User user, String matchStatus, Pageable pageable);
    
    // Find matches with communication history
    @Query("SELECT m FROM Match m WHERE m.user = :user AND (m.hasSentMessage = true OR m.hasReceivedMessage = true OR m.hasCalled = true OR m.hasReceivedCall = true) ORDER BY m.lastInteractionAt DESC")
    List<Match> findMatchesWithCommunicationHistory(@Param("user") User user);
    
    // Find matches that have met in person
    @Query("SELECT m FROM Match m WHERE m.user = :user AND m.hasMetInPerson = true ORDER BY m.lastInteractionAt DESC")
    List<Match> findMatchesMetInPerson(@Param("user") User user);
    
    // Additional count queries
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user")
    Long countByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Match m WHERE m.user = :user AND m.matchStatus = :status")
    Long countByUserAndMatchStatus(@Param("user") User user, @Param("status") String status);
} 