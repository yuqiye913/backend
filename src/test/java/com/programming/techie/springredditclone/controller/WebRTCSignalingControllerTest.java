package com.programming.techie.springredditclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.dto.WebRTCSignalingDto;
import com.programming.techie.springredditclone.dto.WebRTCIceCandidateDto;
import com.programming.techie.springredditclone.dto.WebRTCSessionDescriptionDto;
import com.programming.techie.springredditclone.service.WebRTCSignalingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WebRTCSignalingControllerTest {

    @Mock
    private WebRTCSignalingService webRTCSignalingService;

    @InjectMocks
    private WebRTCSignalingController webRTCSignalingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webRTCSignalingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createSession_ShouldReturnSignalingDto() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCSignalingDto expectedDto = new WebRTCSignalingDto();
        expectedDto.setSessionId(sessionId);
        expectedDto.setRoomId("room_123");
        expectedDto.setPeerId("peer_123");
        expectedDto.setSessionType("video");
        expectedDto.setSessionStatus("active");

        when(webRTCSignalingService.createSession(sessionId)).thenReturn(expectedDto);

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/sessions")
                .param("sessionId", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.roomId").value("room_123"))
                .andExpect(jsonPath("$.peerId").value("peer_123"))
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("active"));

        verify(webRTCSignalingService).createSession(sessionId);
    }

    @Test
    void getServerConfiguration_ShouldReturnConfiguration() throws Exception {
        // Arrange
        WebRTCSignalingDto expectedConfig = new WebRTCSignalingDto();
        expectedConfig.setSessionId("server_config");
        expectedConfig.setSessionType("configuration");
        expectedConfig.setSessionStatus("active");
        expectedConfig.setStunServers(Arrays.asList("stun:stun.l.google.com:19302"));

        when(webRTCSignalingService.getServerConfiguration()).thenReturn(expectedConfig);

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/config")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("server_config"))
                .andExpect(jsonPath("$.sessionType").value("configuration"))
                .andExpect(jsonPath("$.sessionStatus").value("active"))
                .andExpect(jsonPath("$.stunServers[0]").value("stun:stun.l.google.com:19302"));

        verify(webRTCSignalingService).getServerConfiguration();
    }

    @Test
    void generateOffer_ShouldReturnSessionDescription() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCSessionDescriptionDto expectedOffer = new WebRTCSessionDescriptionDto();
        expectedOffer.setSessionId(sessionId);
        expectedOffer.setType("offer");
        expectedOffer.setSdp("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        expectedOffer.setSessionType("video");
        expectedOffer.setSessionStatus("connecting");

        when(webRTCSignalingService.generateOffer(sessionId)).thenReturn(expectedOffer);

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/offer", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.type").value("offer"))
                .andExpect(jsonPath("$.sdp").value("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n"))
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("connecting"));

        verify(webRTCSignalingService).generateOffer(sessionId);
    }

    @Test
    void generateAnswer_ShouldReturnSessionDescription() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        String offerSdp = "v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n";
        WebRTCSessionDescriptionDto expectedAnswer = new WebRTCSessionDescriptionDto();
        expectedAnswer.setSessionId(sessionId);
        expectedAnswer.setType("answer");
        expectedAnswer.setSdp("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        expectedAnswer.setSessionType("video");
        expectedAnswer.setSessionStatus("connecting");

        when(webRTCSignalingService.generateAnswer(sessionId, offerSdp)).thenReturn(expectedAnswer);

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/answer", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(offerSdp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.type").value("answer"))
                .andExpect(jsonPath("$.sdp").value("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n"))
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("connecting"));

        verify(webRTCSignalingService).generateAnswer(sessionId, offerSdp);
    }

    @Test
    void addIceCandidate_ShouldReturnOk() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCIceCandidateDto candidate = new WebRTCIceCandidateDto();
        candidate.setSessionId(sessionId);
        candidate.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");
        candidate.setSdpMid("0");
        candidate.setSdpMLineIndex(0);
        candidate.setType("host");
        candidate.setProtocol("UDP");
        candidate.setIp("192.168.1.1");
        candidate.setPort(12345);

        doNothing().when(webRTCSignalingService).addIceCandidate(sessionId, candidate);

        // Act & Assert
        mockMvc.perform(post("/api/webrtc/{sessionId}/ice-candidate", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isOk());

        verify(webRTCSignalingService).addIceCandidate(sessionId, candidate);
    }

    @Test
    void getIceCandidates_ShouldReturnCandidatesList() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCIceCandidateDto candidate1 = new WebRTCIceCandidateDto();
        candidate1.setSessionId(sessionId);
        candidate1.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");
        candidate1.setSdpMid("0");
        candidate1.setSdpMLineIndex(0);

        WebRTCIceCandidateDto candidate2 = new WebRTCIceCandidateDto();
        candidate2.setSessionId(sessionId);
        candidate2.setCandidate("candidate:2 1 UDP 2122252542 10.0.0.1 54321 typ host");
        candidate2.setSdpMid("0");
        candidate2.setSdpMLineIndex(0);

        List<WebRTCIceCandidateDto> expectedCandidates = Arrays.asList(candidate1, candidate2);

        when(webRTCSignalingService.getIceCandidates(sessionId)).thenReturn(expectedCandidates);

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/ice-candidates", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(sessionId))
                .andExpect(jsonPath("$[0].candidate").value("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host"))
                .andExpect(jsonPath("$[0].sdpMid").value("0"))
                .andExpect(jsonPath("$[0].sdpMLineIndex").value(0))
                .andExpect(jsonPath("$[1].sessionId").value(sessionId))
                .andExpect(jsonPath("$[1].candidate").value("candidate:2 1 UDP 2122252542 10.0.0.1 54321 typ host"))
                .andExpect(jsonPath("$[1].sdpMid").value("0"))
                .andExpect(jsonPath("$[1].sdpMLineIndex").value(0));

        verify(webRTCSignalingService).getIceCandidates(sessionId);
    }

    @Test
    void updateSessionDescription_ShouldReturnOk() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCSessionDescriptionDto description = new WebRTCSessionDescriptionDto();
        description.setSessionId(sessionId);
        description.setType("offer");
        description.setSdp("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        description.setSessionType("video");
        description.setSessionStatus("connecting");

        doNothing().when(webRTCSignalingService).updateSessionDescription(sessionId, description);

        // Act & Assert
        mockMvc.perform(put("/api/webrtc/{sessionId}/session-description", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(description)))
                .andExpect(status().isOk());

        verify(webRTCSignalingService).updateSessionDescription(sessionId, description);
    }

    @Test
    void getSessionDescription_ShouldReturnDescription() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        WebRTCSessionDescriptionDto expectedDescription = new WebRTCSessionDescriptionDto();
        expectedDescription.setSessionId(sessionId);
        expectedDescription.setType("offer");
        expectedDescription.setSdp("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        expectedDescription.setSessionType("video");
        expectedDescription.setSessionStatus("connecting");

        when(webRTCSignalingService.getSessionDescription(sessionId)).thenReturn(expectedDescription);

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/session-description", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.type").value("offer"))
                .andExpect(jsonPath("$.sdp").value("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n"))
                .andExpect(jsonPath("$.sessionType").value("video"))
                .andExpect(jsonPath("$.sessionStatus").value("connecting"));

        verify(webRTCSignalingService).getSessionDescription(sessionId);
    }

    @Test
    void getSessionDescription_ShouldReturnNotFound_WhenDescriptionDoesNotExist() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        when(webRTCSignalingService.getSessionDescription(sessionId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/session-description", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(webRTCSignalingService).getSessionDescription(sessionId);
    }

    @Test
    void isSessionActive_ShouldReturnTrue_WhenSessionIsActive() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        when(webRTCSignalingService.isSessionActive(sessionId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/active", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(webRTCSignalingService).isSessionActive(sessionId);
    }

    @Test
    void isSessionActive_ShouldReturnFalse_WhenSessionIsNotActive() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        when(webRTCSignalingService.isSessionActive(sessionId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/active", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(webRTCSignalingService).isSessionActive(sessionId);
    }

    @Test
    void closeSession_ShouldReturnOk() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        doNothing().when(webRTCSignalingService).closeSession(sessionId);

        // Act & Assert
        mockMvc.perform(delete("/api/webrtc/{sessionId}", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(webRTCSignalingService).closeSession(sessionId);
    }

    @Test
    void pollForUpdates_ShouldReturnSignalingData_WhenSessionIsActive() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        long lastUpdate = 1234567890L;
        
        WebRTCSignalingDto expectedData = new WebRTCSignalingDto();
        expectedData.setSessionId(sessionId);
        expectedData.setSessionStatus("active");
        expectedData.setTimestamp(1234567891L);
        expectedData.setLocalDescription("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        expectedData.setIceCandidates(Arrays.asList("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host"));

        WebRTCSessionDescriptionDto mockDescription = new WebRTCSessionDescriptionDto();
        mockDescription.setTimestamp(1234567890L);
        
        when(webRTCSignalingService.isSessionActive(sessionId)).thenReturn(true);
        when(webRTCSignalingService.getSessionDescription(sessionId)).thenReturn(mockDescription);
        when(webRTCSignalingService.getIceCandidates(sessionId)).thenReturn(Arrays.asList(new WebRTCIceCandidateDto()));

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/poll", sessionId)
                .param("lastUpdate", String.valueOf(lastUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.sessionStatus").value("active"));

        verify(webRTCSignalingService).isSessionActive(sessionId);
        verify(webRTCSignalingService).getSessionDescription(sessionId);
        verify(webRTCSignalingService).getIceCandidates(sessionId);
    }

    @Test
    void pollForUpdates_ShouldReturnNotFound_WhenSessionIsNotActive() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        long lastUpdate = 1234567890L;
        
        when(webRTCSignalingService.isSessionActive(sessionId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/poll", sessionId)
                .param("lastUpdate", String.valueOf(lastUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(webRTCSignalingService).isSessionActive(sessionId);
        verify(webRTCSignalingService, never()).getSessionDescription(anyString());
        verify(webRTCSignalingService, never()).getIceCandidates(anyString());
    }

    @Test
    void pollForUpdates_ShouldUseDefaultLastUpdate_WhenNotProvided() throws Exception {
        // Arrange
        String sessionId = "test_session_123";
        
        WebRTCSessionDescriptionDto mockDescription2 = new WebRTCSessionDescriptionDto();
        mockDescription2.setTimestamp(1234567890L);
        
        when(webRTCSignalingService.isSessionActive(sessionId)).thenReturn(true);
        when(webRTCSignalingService.getSessionDescription(sessionId)).thenReturn(mockDescription2);
        when(webRTCSignalingService.getIceCandidates(sessionId)).thenReturn(Arrays.asList(new WebRTCIceCandidateDto()));

        // Act & Assert
        mockMvc.perform(get("/api/webrtc/{sessionId}/poll", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(webRTCSignalingService).isSessionActive(sessionId);
        verify(webRTCSignalingService).getSessionDescription(sessionId);
        verify(webRTCSignalingService).getIceCandidates(sessionId);
    }
} 