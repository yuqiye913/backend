package com.programming.techie.springredditclone.repository;

import com.programming.techie.springredditclone.model.VideoCallSession;
import com.programming.techie.springredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoCallSessionRepository extends JpaRepository<VideoCallSession, Long> {
    
    // Find by session ID
    Optional<VideoCallSession> findBySessionId(String sessionId);
    
    // Find by caller
    List<VideoCallSession> findByCaller(User caller);
    
    // Find by receiver
    List<VideoCallSession> findByReceiver(User receiver);
    
    // Find by call status
    List<VideoCallSession> findByCallStatus(String callStatus);
    
    // Find by call type
    List<VideoCallSession> findByCallType(String callType);
    
    // Find active calls for a user (either as caller or receiver)
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus IN ('initiated', 'ringing', 'connecting', 'connected', 'reconnecting') ORDER BY vcs.createdAt DESC")
    List<VideoCallSession> findActiveCallsByUser(@Param("user") User user);
    
    // Find completed calls for a user
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus IN ('ended', 'missed', 'declined') ORDER BY vcs.callEndedAt DESC")
    List<VideoCallSession> findCompletedCallsByUser(@Param("user") User user);
    
    // Find calls between two users
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE (vcs.caller = :user1 AND vcs.receiver = :user2) OR (vcs.caller = :user2 AND vcs.receiver = :user1) ORDER BY vcs.createdAt DESC")
    List<VideoCallSession> findCallsBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    // Find calls by match
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE vcs.match.matchId = :matchId ORDER BY vcs.createdAt DESC")
    List<VideoCallSession> findByMatchId(@Param("matchId") Long matchId);
    
    // Find calls by room ID
    Optional<VideoCallSession> findByRoomId(String roomId);
    
    // Find calls by peer ID
    List<VideoCallSession> findByPeerId(String peerId);
    
    // Find calls created after a specific time
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE vcs.createdAt >= :since ORDER BY vcs.createdAt DESC")
    List<VideoCallSession> findCallsCreatedAfter(@Param("since") Instant since);
    
    // Find calls with errors
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE vcs.hasError = true ORDER BY vcs.updatedAt DESC")
    List<VideoCallSession> findCallsWithErrors();
    
    // Find calls by quality score
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE vcs.overallQualityScore >= :minScore ORDER BY vcs.overallQualityScore DESC")
    List<VideoCallSession> findCallsByQualityScore(@Param("minScore") Double minScore);
    
    // Find calls by duration range
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE vcs.callDuration BETWEEN :minDuration AND :maxDuration ORDER BY vcs.callDuration DESC")
    List<VideoCallSession> findCallsByDurationRange(@Param("minDuration") Long minDuration, @Param("maxDuration") Long maxDuration);
    
    // Count active calls for a user
    @Query("SELECT COUNT(vcs) FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus IN ('initiated', 'ringing', 'connecting', 'connected', 'reconnecting')")
    Long countActiveCallsByUser(@Param("user") User user);
    
    // Count completed calls for a user
    @Query("SELECT COUNT(vcs) FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus IN ('ended', 'missed', 'declined')")
    Long countCompletedCallsByUser(@Param("user") User user);
    
    // Count successful calls for a user
    @Query("SELECT COUNT(vcs) FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus = 'ended'")
    Long countSuccessfulCallsByUser(@Param("user") User user);
    
    // Count missed calls for a user
    @Query("SELECT COUNT(vcs) FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus = 'missed'")
    Long countMissedCallsByUser(@Param("user") User user);
    
    // Count declined calls for a user
    @Query("SELECT COUNT(vcs) FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus = 'declined'")
    Long countDeclinedCallsByUser(@Param("user") User user);
    
    // Get total call duration for a user
    @Query("SELECT COALESCE(SUM(vcs.callDuration), 0) FROM VideoCallSession vcs WHERE (vcs.caller = :user OR vcs.receiver = :user) AND vcs.callStatus = 'ended'")
    Long getTotalCallDurationByUser(@Param("user") User user);
    
    // Find calls with specific video quality
    List<VideoCallSession> findByVideoQuality(String videoQuality);
    
    // Find calls with specific audio quality
    List<VideoCallSession> findByAudioQuality(String audioQuality);
    
    // Find calls by network type
    List<VideoCallSession> findByNetworkType(String networkType);
    
    // Find calls by device type
    List<VideoCallSession> findByDeviceType(String deviceType);
    
    // Find calls by connection quality
    List<VideoCallSession> findByConnectionQuality(String connectionQuality);
    
    // Find calls by privacy level
    List<VideoCallSession> findByPrivacyLevel(String privacyLevel);
    
    // Find calls by security level
    List<VideoCallSession> findBySecurityLevel(String securityLevel);
    
    // Find calls by encryption type
    List<VideoCallSession> findByEncryptionType(String encryptionType);
    
    // Find calls by call purpose
    List<VideoCallSession> findByCallPurpose(String callPurpose);
    
    // Find scheduled calls
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE vcs.isScheduled = true AND vcs.scheduledTime >= :now ORDER BY vcs.scheduledTime ASC")
    List<VideoCallSession> findUpcomingScheduledCalls(@Param("now") Instant now);
    
    // Find calls that need attention (errors, long duration, etc.)
    @Query("SELECT vcs FROM VideoCallSession vcs WHERE vcs.hasError = true OR vcs.callDuration > :maxDuration ORDER BY vcs.updatedAt DESC")
    List<VideoCallSession> findCallsNeedingAttention(@Param("maxDuration") Long maxDuration);
} 