package com.programming.techie.springredditclone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.dto.WebRTCSignalingDto;
import com.programming.techie.springredditclone.dto.WebRTCIceCandidateDto;
import com.programming.techie.springredditclone.dto.WebRTCSessionDescriptionDto;
import com.programming.techie.springredditclone.model.VideoCallSession;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.Match;
import com.programming.techie.springredditclone.repository.VideoCallSessionRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class WebRTCSignalingIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VideoCallSessionRepository videoCallSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private VideoCallSession testSession;
    private User testUser;
    private Match testMatch;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setEnabled(true);
        testUser = userRepository.save(testUser);

        // Create test match
        testMatch = new Match();
        testMatch.setUser(testUser);
        testMatch.setMatchedUser(testUser);
        testMatch.setMatchStatus("active");
        testMatch.setMatchedAt(Instant.now());
        testMatch = matchRepository.save(testMatch);

        // Create test video call session
        testSession = new VideoCallSession();
        testSession.setSessionId("test_session_123");
        testSession.setRoomId("room_123");
        testSession.setPeerId("peer_123");
        testSession.setCaller(testUser);
        testSession.setReceiver(testUser);
        testSession.setMatch(testMatch);
        testSession.setCallType("video");
        testSession.setCallStatus("active");
        testSession.setCreatedAt(Instant.now());
        testSession = videoCallSessionRepository.save(testSession);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSession_ShouldCreateNewSignalingSession() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/webrtc/sessions")
                .param("sessionId", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(testSession.getSessionId()))
                .andExpect(jsonPath("$.roomId").value("room_123"))
                .andExpect(jsonPath("$.peerId").value("peer_123"))
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("active"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.stunServers").isArray())
                .andExpect(jsonPath("$.iceServers").exists())
                .andExpect(jsonPath("$.rtcConfiguration").exists())
                .andExpect(jsonPath("$.constraints").exists())
                .andExpect(jsonPath("$.videoCodec").value("H.264"))
                .andExpect(jsonPath("$.audioCodec").value("Opus"))
                .andExpect(jsonPath("$.videoResolution").value("1280x720"))
                .andExpect(jsonPath("$.videoFrameRate").value("30fps"))
                .andExpect(jsonPath("$.audioSampleRate").value("48kHz"))
                .andExpect(jsonPath("$.audioChannels").value("stereo"))
                .andExpect(jsonPath("$.isEncrypted").value(true))
                .andExpect(jsonPath("$.encryptionType").value("DTLS"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getServerConfiguration_ShouldReturnConfiguration() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/webrtc/config")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("server_config"))
                .andExpect(jsonPath("$.sessionType").value("configuration"))
                .andExpect(jsonPath("$.sessionStatus").value("active"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.stunServers").isArray())
                .andExpect(jsonPath("$.iceServers").exists())
                .andExpect(jsonPath("$.rtcConfiguration").exists())
                .andExpect(jsonPath("$.constraints").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateOffer_ShouldReturnValidOffer() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/offer", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(testSession.getSessionId()))
                .andExpect(jsonPath("$.type").value("offer"))
                .andExpect(jsonPath("$.sdp").exists())
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("connecting"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.videoCodec").value("H.264"))
                .andExpect(jsonPath("$.audioCodec").value("Opus"))
                .andExpect(jsonPath("$.videoResolution").value("1280x720"))
                .andExpect(jsonPath("$.videoFrameRate").value("30fps"))
                .andExpect(jsonPath("$.audioSampleRate").value("48kHz"))
                .andExpect(jsonPath("$.audioChannels").value("stereo"))
                .andExpect(jsonPath("$.videoBitrate").value("1000"))
                .andExpect(jsonPath("$.audioBitrate").value("64"))
                .andExpect(jsonPath("$.videoQuality").value("high"))
                .andExpect(jsonPath("$.audioQuality").value("high"))
                .andExpect(jsonPath("$.networkType").value("wifi"))
                .andExpect(jsonPath("$.bandwidth").value("1000"))
                .andExpect(jsonPath("$.latency").value("50"))
                .andExpect(jsonPath("$.isEncrypted").value(true))
                .andExpect(jsonPath("$.encryptionType").value("DTLS"))
                .andExpect(jsonPath("$.iceServers").exists())
                .andExpect(jsonPath("$.iceTransportPolicy").value("all"))
                .andExpect(jsonPath("$.bundlePolicy").value("max-bundle"))
                .andExpect(jsonPath("$.rtcpMuxPolicy").value("require"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateAnswer_ShouldReturnValidAnswer() throws Exception {
        // Arrange
        String offerSdp = "v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n";

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/answer", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(offerSdp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(testSession.getSessionId()))
                .andExpect(jsonPath("$.type").value("answer"))
                .andExpect(jsonPath("$.sdp").exists())
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("connecting"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.videoCodec").value("H.264"))
                .andExpect(jsonPath("$.audioCodec").value("Opus"))
                .andExpect(jsonPath("$.videoResolution").value("1280x720"))
                .andExpect(jsonPath("$.videoFrameRate").value("30fps"))
                .andExpect(jsonPath("$.audioSampleRate").value("48kHz"))
                .andExpect(jsonPath("$.audioChannels").value("stereo"))
                .andExpect(jsonPath("$.videoBitrate").value("1000"))
                .andExpect(jsonPath("$.audioBitrate").value("64"))
                .andExpect(jsonPath("$.videoQuality").value("high"))
                .andExpect(jsonPath("$.audioQuality").value("high"))
                .andExpect(jsonPath("$.networkType").value("wifi"))
                .andExpect(jsonPath("$.bandwidth").value("1000"))
                .andExpect(jsonPath("$.latency").value("50"))
                .andExpect(jsonPath("$.isEncrypted").value(true))
                .andExpect(jsonPath("$.encryptionType").value("DTLS"))
                .andExpect(jsonPath("$.iceServers").exists())
                .andExpect(jsonPath("$.iceTransportPolicy").value("all"))
                .andExpect(jsonPath("$.bundlePolicy").value("max-bundle"))
                .andExpect(jsonPath("$.rtcpMuxPolicy").value("require"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addIceCandidate_ShouldAddCandidateSuccessfully() throws Exception {
        // Arrange
        WebRTCIceCandidateDto candidate = new WebRTCIceCandidateDto();
        candidate.setSessionId(testSession.getSessionId());
        candidate.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");
        candidate.setSdpMid("0");
        candidate.setSdpMLineIndex(0);
        candidate.setType("host");
        candidate.setProtocol("UDP");
        candidate.setIp("192.168.1.1");
        candidate.setPort(12345);

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/ice-candidate", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isOk());

        // Verify candidate was added by getting candidates
        mockMvc.perform(get("/api/webrtc/{sessionId}/ice-candidates", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(testSession.getSessionId()))
                .andExpect(jsonPath("$[0].candidate").value("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host"))
                .andExpect(jsonPath("$[0].sdpMid").value("0"))
                .andExpect(jsonPath("$[0].sdpMLineIndex").value(0))
                .andExpect(jsonPath("$[0].type").value("host"))
                .andExpect(jsonPath("$[0].protocol").value("UDP"))
                .andExpect(jsonPath("$[0].ip").value("192.168.1.1"))
                .andExpect(jsonPath("$[0].port").value(12345))
                .andExpect(jsonPath("$[0].timestamp").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateSessionDescription_ShouldUpdateDescriptionSuccessfully() throws Exception {
        // Arrange
        WebRTCSessionDescriptionDto description = new WebRTCSessionDescriptionDto();
        description.setSessionId(testSession.getSessionId());
        description.setType("offer");
        description.setSdp("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        description.setSessionType("video");
        description.setSessionStatus("connecting");

        // Act & Assert
        mockMvc.perform(put("/api/webrtc/{sessionId}/session-description", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(description)))
                .andExpect(status().isOk());

        // Verify description was updated by getting it
        mockMvc.perform(get("/api/webrtc/{sessionId}/session-description", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(testSession.getSessionId()))
                .andExpect(jsonPath("$.type").value("offer"))
                .andExpect(jsonPath("$.sdp").value("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n"))
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("connecting"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSessionDescription_ShouldReturnNotFound_WhenDescriptionDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/session-description", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void isSessionActive_ShouldReturnTrue_WhenSessionIsActive() throws Exception {
        // First create the session
        mockMvc.perform(post("/api/webrtc/sessions")
                .param("sessionId", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/active", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void isSessionActive_ShouldReturnFalse_WhenSessionIsNotActive() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/active", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void closeSession_ShouldCloseSessionSuccessfully() throws Exception {
        // First create the session
        mockMvc.perform(post("/api/webrtc/sessions")
                .param("sessionId", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify session is active
        mockMvc.perform(get("/api/webrtc/{sessionId}/active", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Act
        mockMvc.perform(delete("/api/webrtc/{sessionId}", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert session is no longer active
        mockMvc.perform(get("/api/webrtc/{sessionId}/active", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void pollForUpdates_ShouldReturnSignalingData_WhenSessionIsActive() throws Exception {
        // First create the session
        mockMvc.perform(post("/api/webrtc/sessions")
                .param("sessionId", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/poll", testSession.getSessionId())
                .param("lastUpdate", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(testSession.getSessionId()))
                .andExpect(jsonPath("$.sessionStatus").value("active"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void pollForUpdates_ShouldReturnNotFound_WhenSessionIsNotActive() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/poll", testSession.getSessionId())
                .param("lastUpdate", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void pollForUpdates_ShouldUseDefaultLastUpdate_WhenNotProvided() throws Exception {
        // First create the session
        mockMvc.perform(post("/api/webrtc/sessions")
                .param("sessionId", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/poll", testSession.getSessionId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(testSession.getSessionId()))
                .andExpect(jsonPath("$.sessionStatus").value("active"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createSession_ShouldReturnError_WhenSessionNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/webrtc/sessions")
                .param("sessionId", "non_existent_session")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateOffer_ShouldReturnError_WhenSessionNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/offer", "non_existent_session")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateAnswer_ShouldReturnError_WhenSessionNotFound() throws Exception {
        // Arrange
        String offerSdp = "v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n";

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/answer", "non_existent_session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(offerSdp))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "testuser")
    void addIceCandidate_ShouldReturnError_WhenSessionNotFound() throws Exception {
        // Arrange
        WebRTCIceCandidateDto candidate = new WebRTCIceCandidateDto();
        candidate.setSessionId("non_existent_session");
        candidate.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");
        candidate.setSdpMid("0");
        candidate.setSdpMLineIndex(0);

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/ice-candidate", "non_existent_session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isInternalServerError());
    }
} 