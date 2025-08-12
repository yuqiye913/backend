package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.WebRTCSignalingDto;
import com.programming.techie.springredditclone.dto.WebRTCIceCandidateDto;
import com.programming.techie.springredditclone.dto.WebRTCSessionDescriptionDto;
import com.programming.techie.springredditclone.model.VideoCallSession;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.repository.VideoCallSessionRepository;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import com.programming.techie.springredditclone.service.impl.WebRTCSignalingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebRTCSignalingServiceTest {

    @Mock
    private VideoCallSessionRepository videoCallSessionRepository;

    @InjectMocks
    private WebRTCSignalingServiceImpl webRTCSignalingService;

    private VideoCallSession mockVideoCallSession;
    private User mockUser;
    private Match mockMatch;

    @BeforeEach
    void setUp() {
        // Create mock user
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        // Create mock match
        mockMatch = new Match();
        mockMatch.setMatchId(1L);
        mockMatch.setUser(mockUser);
        mockMatch.setMatchedUser(mockUser);

        // Create mock video call session
        mockVideoCallSession = new VideoCallSession();
        mockVideoCallSession.setSessionId("test_session_123");
        mockVideoCallSession.setRoomId("room_123");
        mockVideoCallSession.setPeerId("peer_123");
        mockVideoCallSession.setCaller(mockUser);
        mockVideoCallSession.setReceiver(mockUser);
        mockVideoCallSession.setMatch(mockMatch);
        mockVideoCallSession.setCallType("video");
        mockVideoCallSession.setCallStatus("active");
        mockVideoCallSession.setCreatedAt(Instant.now());
    }

    @Test
    void createSession_ShouldCreateNewSignalingSession() {
        // Arrange
        String sessionId = "test_session_123";
        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Act
        WebRTCSignalingDto result = webRTCSignalingService.createSession(sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
        assertEquals("room_123", result.getRoomId());
        assertEquals("peer_123", result.getPeerId());
        assertEquals("video", result.getSessionType());
        assertEquals("active", result.getSessionStatus());
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getStunServers());
        assertNotNull(result.getIceServers());
        assertNotNull(result.getRtcConfiguration());
        assertNotNull(result.getConstraints());
        assertEquals("H.264", result.getVideoCodec());
        assertEquals("Opus", result.getAudioCodec());
        assertTrue(result.isEncrypted());
        assertEquals("DTLS", result.getEncryptionType());

        verify(videoCallSessionRepository).findBySessionId(sessionId);
    }

    @Test
    void createSession_ShouldThrowException_WhenSessionNotFound() {
        // Arrange
        String sessionId = "non_existent_session";
        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SpringRedditException.class, () -> {
            webRTCSignalingService.createSession(sessionId);
        });

        verify(videoCallSessionRepository).findBySessionId(sessionId);
    }

    @Test
    void generateOffer_ShouldGenerateValidOffer() {
        // Arrange
        String sessionId = "test_session_123";
        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Act
        WebRTCSessionDescriptionDto result = webRTCSignalingService.generateOffer(sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
        assertEquals("offer", result.getType());
        assertEquals("video", result.getSessionType());
        assertEquals("connecting", result.getSessionStatus());
        assertNotNull(result.getSdp());
        assertNotNull(result.getTimestamp());
        assertEquals("H.264", result.getVideoCodec());
        assertEquals("Opus", result.getAudioCodec());
        assertEquals("1280x720", result.getVideoResolution());
        assertEquals("30fps", result.getVideoFrameRate());
        assertEquals("48kHz", result.getAudioSampleRate());
        assertEquals("stereo", result.getAudioChannels());
        assertEquals("high", result.getVideoQuality());
        assertEquals("high", result.getAudioQuality());
        assertTrue(result.isEncrypted());
        assertEquals("DTLS", result.getEncryptionType());

        verify(videoCallSessionRepository).findBySessionId(sessionId);
    }

    @Test
    void generateAnswer_ShouldGenerateValidAnswer() {
        // Arrange
        String sessionId = "test_session_123";
        String offerSdp = "v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n";
        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Act
        WebRTCSessionDescriptionDto result = webRTCSignalingService.generateAnswer(sessionId, offerSdp);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
        assertEquals("answer", result.getType());
        assertEquals("video", result.getSessionType());
        assertEquals("connecting", result.getSessionStatus());
        assertNotNull(result.getSdp());
        assertNotNull(result.getTimestamp());
        assertEquals("H.264", result.getVideoCodec());
        assertEquals("Opus", result.getAudioCodec());
        assertEquals("1280x720", result.getVideoResolution());
        assertEquals("30fps", result.getVideoFrameRate());
        assertEquals("48kHz", result.getAudioSampleRate());
        assertEquals("stereo", result.getAudioChannels());
        assertEquals("high", result.getVideoQuality());
        assertEquals("high", result.getAudioQuality());
        assertTrue(result.isEncrypted());
        assertEquals("DTLS", result.getEncryptionType());

        verify(videoCallSessionRepository).findBySessionId(sessionId);
    }

    @Test
    void addIceCandidate_ShouldAddCandidateSuccessfully() {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCIceCandidateDto candidate = new WebRTCIceCandidateDto();
        candidate.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");
        candidate.setSdpMid("0");
        candidate.setSdpMLineIndex(0);
        candidate.setType("host");
        candidate.setProtocol("UDP");
        candidate.setIp("192.168.1.1");
        candidate.setPort(12345);

        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Act
        webRTCSignalingService.addIceCandidate(sessionId, candidate);

        // Assert
        verify(videoCallSessionRepository).findBySessionId(sessionId);
        
        // Verify candidate was added by getting candidates
        List<WebRTCIceCandidateDto> candidates = webRTCSignalingService.getIceCandidates(sessionId);
        assertNotNull(candidates);
        assertEquals(1, candidates.size());
        assertEquals(candidate.getCandidate(), candidates.get(0).getCandidate());
        assertEquals(candidate.getSdpMid(), candidates.get(0).getSdpMid());
        assertEquals(candidate.getSdpMLineIndex(), candidates.get(0).getSdpMLineIndex());
    }

    @Test
    void getIceCandidates_ShouldReturnEmptyList_WhenNoCandidates() {
        // Arrange
        String sessionId = "test_session_123";

        // Act
        List<WebRTCIceCandidateDto> result = webRTCSignalingService.getIceCandidates(sessionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getIceCandidates_ShouldReturnCandidates_WhenCandidatesExist() {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCIceCandidateDto candidate1 = new WebRTCIceCandidateDto();
        candidate1.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");
        candidate1.setSdpMid("0");
        candidate1.setSdpMLineIndex(0);

        WebRTCIceCandidateDto candidate2 = new WebRTCIceCandidateDto();
        candidate2.setCandidate("candidate:2 1 UDP 2122252542 10.0.0.1 54321 typ host");
        candidate2.setSdpMid("0");
        candidate2.setSdpMLineIndex(0);

        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Add candidates
        webRTCSignalingService.addIceCandidate(sessionId, candidate1);
        webRTCSignalingService.addIceCandidate(sessionId, candidate2);

        // Act
        List<WebRTCIceCandidateDto> result = webRTCSignalingService.getIceCandidates(sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getCandidate().equals(candidate1.getCandidate())));
        assertTrue(result.stream().anyMatch(c -> c.getCandidate().equals(candidate2.getCandidate())));
    }

    @Test
    void updateSessionDescription_ShouldUpdateDescriptionSuccessfully() {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCSessionDescriptionDto description = new WebRTCSessionDescriptionDto();
        description.setSessionId(sessionId);
        description.setType("offer");
        description.setSdp("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        description.setSessionType("video");
        description.setSessionStatus("connecting");

        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Act
        webRTCSignalingService.updateSessionDescription(sessionId, description);

        // Assert
        verify(videoCallSessionRepository).findBySessionId(sessionId);
        
        // Verify description was updated by getting it
        WebRTCSessionDescriptionDto result = webRTCSignalingService.getSessionDescription(sessionId);
        assertNotNull(result);
        assertEquals(description.getSdp(), result.getSdp());
        assertEquals(description.getType(), result.getType());
    }

    @Test
    void getSessionDescription_ShouldReturnNull_WhenNoDescriptionExists() {
        // Arrange
        String sessionId = "test_session_123";

        // Act
        WebRTCSessionDescriptionDto result = webRTCSignalingService.getSessionDescription(sessionId);

        // Assert
        assertNull(result);
    }

    @Test
    void getSessionDescription_ShouldReturnAnswer_WhenBothOfferAndAnswerExist() {
        // Arrange
        String sessionId = "test_session_123";
        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Create offer and answer
        WebRTCSessionDescriptionDto offer = new WebRTCSessionDescriptionDto();
        offer.setSessionId(sessionId);
        offer.setType("offer");
        offer.setSdp("offer sdp");

        WebRTCSessionDescriptionDto answer = new WebRTCSessionDescriptionDto();
        answer.setSessionId(sessionId);
        answer.setType("answer");
        answer.setSdp("answer sdp");

        webRTCSignalingService.updateSessionDescription(sessionId, offer);
        webRTCSignalingService.updateSessionDescription(sessionId, answer);

        // Act
        WebRTCSessionDescriptionDto result = webRTCSignalingService.getSessionDescription(sessionId);

        // Assert
        assertNotNull(result);
        assertEquals("answer", result.getType());
        assertEquals("answer sdp", result.getSdp());
    }

    @Test
    void closeSession_ShouldRemoveSessionSuccessfully() {
        // Arrange
        String sessionId = "test_session_123";
        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        // Create session and add some data
        webRTCSignalingService.createSession(sessionId);
        WebRTCIceCandidateDto candidate = new WebRTCIceCandidateDto();
        candidate.setCandidate("test candidate");
        webRTCSignalingService.addIceCandidate(sessionId, candidate);

        // Verify session is active
        assertTrue(webRTCSignalingService.isSessionActive(sessionId));
        assertFalse(webRTCSignalingService.getIceCandidates(sessionId).isEmpty());

        // Act
        webRTCSignalingService.closeSession(sessionId);

        // Assert
        assertFalse(webRTCSignalingService.isSessionActive(sessionId));
        assertTrue(webRTCSignalingService.getIceCandidates(sessionId).isEmpty());
        assertNull(webRTCSignalingService.getSessionDescription(sessionId));
    }

    @Test
    void isSessionActive_ShouldReturnFalse_WhenSessionDoesNotExist() {
        // Arrange
        String sessionId = "non_existent_session";

        // Act
        boolean result = webRTCSignalingService.isSessionActive(sessionId);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSessionActive_ShouldReturnTrue_WhenSessionExists() {
        // Arrange
        String sessionId = "test_session_123";
        when(videoCallSessionRepository.findBySessionId(sessionId))
                .thenReturn(Optional.of(mockVideoCallSession));

        webRTCSignalingService.createSession(sessionId);

        // Act
        boolean result = webRTCSignalingService.isSessionActive(sessionId);

        // Assert
        assertTrue(result);
    }

    @Test
    void getServerConfiguration_ShouldReturnValidConfiguration() {
        // Act
        WebRTCSignalingDto result = webRTCSignalingService.getServerConfiguration();

        // Assert
        assertNotNull(result);
        assertEquals("server_config", result.getSessionId());
        assertEquals("configuration", result.getSessionType());
        assertEquals("active", result.getSessionStatus());
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getStunServers());
        assertNotNull(result.getIceServers());
        assertNotNull(result.getRtcConfiguration());
        assertNotNull(result.getConstraints());
        assertTrue(result.getStunServers().contains("stun:stun.l.google.com:19302"));
        assertTrue(result.getStunServers().contains("stun:stun1.l.google.com:19302"));
    }


} 