package com.programming.techie.springredditclone.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebRTCDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void WebRTCSignalingDto_ShouldSerializeAndDeserializeCorrectly() throws Exception {
        // Arrange
        WebRTCSignalingDto originalDto = new WebRTCSignalingDto();
        originalDto.setSessionId("test_session_123");
        originalDto.setRoomId("room_123");
        originalDto.setPeerId("peer_123");
        originalDto.setSessionType("video");
        originalDto.setSessionStatus("active");
        originalDto.setTimestamp(1234567890L);
        originalDto.setStunServers(Arrays.asList("stun:stun.l.google.com:19302", "stun:stun1.l.google.com:19302"));
        originalDto.setIceServers("[{\"urls\":\"stun:stun.l.google.com:19302\"}]");
        originalDto.setRtcConfiguration("{\"iceServers\":[{\"urls\":\"stun:stun.l.google.com:19302\"}]}");
        originalDto.setConstraints("{\"video\":{\"width\":{\"ideal\":1280}}}");
        originalDto.setLocalDescription("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        originalDto.setIceCandidates(Arrays.asList("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host"));
        originalDto.setVideoCodec("H.264");
        originalDto.setAudioCodec("Opus");
        originalDto.setVideoResolution("1280x720");
        originalDto.setVideoFrameRate("30fps");
        originalDto.setAudioSampleRate("48kHz");
        originalDto.setAudioChannels("stereo");
        originalDto.setEncrypted(true);
        originalDto.setEncryptionType("DTLS");
        originalDto.setNetworkType("wifi");
        originalDto.setBandwidth("1000");
        originalDto.setLatency("50");
        originalDto.setErrorCode("NO_ERROR");
        originalDto.setErrorMessage("No error");
        originalDto.setHasError(false);

        // Act
        String json = objectMapper.writeValueAsString(originalDto);
        WebRTCSignalingDto deserializedDto = objectMapper.readValue(json, WebRTCSignalingDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals(originalDto.getSessionId(), deserializedDto.getSessionId());
        assertEquals(originalDto.getRoomId(), deserializedDto.getRoomId());
        assertEquals(originalDto.getPeerId(), deserializedDto.getPeerId());
        assertEquals(originalDto.getSessionType(), deserializedDto.getSessionType());
        assertEquals(originalDto.getSessionStatus(), deserializedDto.getSessionStatus());
        assertEquals(originalDto.getTimestamp(), deserializedDto.getTimestamp());
        assertEquals(originalDto.getStunServers(), deserializedDto.getStunServers());
        assertEquals(originalDto.getIceServers(), deserializedDto.getIceServers());
        assertEquals(originalDto.getRtcConfiguration(), deserializedDto.getRtcConfiguration());
        assertEquals(originalDto.getConstraints(), deserializedDto.getConstraints());
        assertEquals(originalDto.getLocalDescription(), deserializedDto.getLocalDescription());
        assertEquals(originalDto.getIceCandidates(), deserializedDto.getIceCandidates());
        assertEquals(originalDto.getVideoCodec(), deserializedDto.getVideoCodec());
        assertEquals(originalDto.getAudioCodec(), deserializedDto.getAudioCodec());
        assertEquals(originalDto.getVideoResolution(), deserializedDto.getVideoResolution());
        assertEquals(originalDto.getVideoFrameRate(), deserializedDto.getVideoFrameRate());
        assertEquals(originalDto.getAudioSampleRate(), deserializedDto.getAudioSampleRate());
        assertEquals(originalDto.getAudioChannels(), deserializedDto.getAudioChannels());
        assertEquals(originalDto.isEncrypted(), deserializedDto.isEncrypted());
        assertEquals(originalDto.getEncryptionType(), deserializedDto.getEncryptionType());
        assertEquals(originalDto.getNetworkType(), deserializedDto.getNetworkType());
        assertEquals(originalDto.getBandwidth(), deserializedDto.getBandwidth());
        assertEquals(originalDto.getLatency(), deserializedDto.getLatency());
        assertEquals(originalDto.getErrorCode(), deserializedDto.getErrorCode());
        assertEquals(originalDto.getErrorMessage(), deserializedDto.getErrorMessage());
        assertEquals(originalDto.isHasError(), deserializedDto.isHasError());
    }

    @Test
    void WebRTCSessionDescriptionDto_ShouldSerializeAndDeserializeCorrectly() throws Exception {
        // Arrange
        WebRTCSessionDescriptionDto originalDto = new WebRTCSessionDescriptionDto();
        originalDto.setSessionId("test_session_123");
        originalDto.setType("offer");
        originalDto.setSdp("v=0\r\no=- 1234567890 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n");
        originalDto.setSessionType("video");
        originalDto.setSessionStatus("connecting");
        originalDto.setTimestamp(1234567890L);
        originalDto.setVideoCodec("H.264");
        originalDto.setAudioCodec("Opus");
        originalDto.setVideoResolution("1280x720");
        originalDto.setVideoFrameRate("30fps");
        originalDto.setAudioSampleRate("48kHz");
        originalDto.setAudioChannels("stereo");
        originalDto.setVideoBitrate("1000");
        originalDto.setAudioBitrate("64");
        originalDto.setVideoQuality("high");
        originalDto.setAudioQuality("high");
        originalDto.setNetworkType("wifi");
        originalDto.setBandwidth("1000");
        originalDto.setLatency("50");
        originalDto.setEncrypted(true);
        originalDto.setEncryptionType("DTLS");
        originalDto.setIceServers("[{\"urls\":\"stun:stun.l.google.com:19302\"}]");
        originalDto.setIceTransportPolicy("all");
        originalDto.setBundlePolicy("max-bundle");
        originalDto.setRtcpMuxPolicy("require");
        originalDto.setVideoConstraints("{\"width\":{\"ideal\":1280}}");
        originalDto.setAudioConstraints("{\"sampleRate\":{\"ideal\":48000}}");

        // Act
        String json = objectMapper.writeValueAsString(originalDto);
        WebRTCSessionDescriptionDto deserializedDto = objectMapper.readValue(json, WebRTCSessionDescriptionDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals(originalDto.getSessionId(), deserializedDto.getSessionId());
        assertEquals(originalDto.getType(), deserializedDto.getType());
        assertEquals(originalDto.getSdp(), deserializedDto.getSdp());
        assertEquals(originalDto.getSessionType(), deserializedDto.getSessionType());
        assertEquals(originalDto.getSessionStatus(), deserializedDto.getSessionStatus());
        assertEquals(originalDto.getTimestamp(), deserializedDto.getTimestamp());
        assertEquals(originalDto.getVideoCodec(), deserializedDto.getVideoCodec());
        assertEquals(originalDto.getAudioCodec(), deserializedDto.getAudioCodec());
        assertEquals(originalDto.getVideoResolution(), deserializedDto.getVideoResolution());
        assertEquals(originalDto.getVideoFrameRate(), deserializedDto.getVideoFrameRate());
        assertEquals(originalDto.getAudioSampleRate(), deserializedDto.getAudioSampleRate());
        assertEquals(originalDto.getAudioChannels(), deserializedDto.getAudioChannels());
        assertEquals(originalDto.getVideoBitrate(), deserializedDto.getVideoBitrate());
        assertEquals(originalDto.getAudioBitrate(), deserializedDto.getAudioBitrate());
        assertEquals(originalDto.getVideoQuality(), deserializedDto.getVideoQuality());
        assertEquals(originalDto.getAudioQuality(), deserializedDto.getAudioQuality());
        assertEquals(originalDto.getNetworkType(), deserializedDto.getNetworkType());
        assertEquals(originalDto.getBandwidth(), deserializedDto.getBandwidth());
        assertEquals(originalDto.getLatency(), deserializedDto.getLatency());
        assertEquals(originalDto.isEncrypted(), deserializedDto.isEncrypted());
        assertEquals(originalDto.getEncryptionType(), deserializedDto.getEncryptionType());
        assertEquals(originalDto.getIceServers(), deserializedDto.getIceServers());
        assertEquals(originalDto.getIceTransportPolicy(), deserializedDto.getIceTransportPolicy());
        assertEquals(originalDto.getBundlePolicy(), deserializedDto.getBundlePolicy());
        assertEquals(originalDto.getRtcpMuxPolicy(), deserializedDto.getRtcpMuxPolicy());
        assertEquals(originalDto.getVideoConstraints(), deserializedDto.getVideoConstraints());
        assertEquals(originalDto.getAudioConstraints(), deserializedDto.getAudioConstraints());
    }

    @Test
    void WebRTCIceCandidateDto_ShouldSerializeAndDeserializeCorrectly() throws Exception {
        // Arrange
        WebRTCIceCandidateDto originalDto = new WebRTCIceCandidateDto();
        originalDto.setSessionId("test_session_123");
        originalDto.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");
        originalDto.setSdpMid("0");
        originalDto.setSdpMLineIndex(0);
        originalDto.setUsernameFragment("userfrag");
        originalDto.setFoundation("foundation1");
        originalDto.setComponent("RTP");
        originalDto.setProtocol("UDP");
        originalDto.setPriority("2122252543");
        originalDto.setIp("192.168.1.1");
        originalDto.setPort(12345);
        originalDto.setType("host");
        originalDto.setNetworkType("wifi");
        originalDto.setNetworkCost("low");
        originalDto.setAddressFamily("IPv4");
        originalDto.setBandwidth("1000");
        originalDto.setLatency("50");
        originalDto.setPacketLoss("0.1");
        originalDto.setJitter("5");
        originalDto.setEncrypted(true);
        originalDto.setEncryptionType("DTLS");
        originalDto.setCertificate("certificate_data");
        originalDto.setTimestamp(1234567890L);
        originalDto.setErrorCode("NO_ERROR");
        originalDto.setErrorMessage("No error");
        originalDto.setHasError(false);

        // Act
        String json = objectMapper.writeValueAsString(originalDto);
        WebRTCIceCandidateDto deserializedDto = objectMapper.readValue(json, WebRTCIceCandidateDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals(originalDto.getSessionId(), deserializedDto.getSessionId());
        assertEquals(originalDto.getCandidate(), deserializedDto.getCandidate());
        assertEquals(originalDto.getSdpMid(), deserializedDto.getSdpMid());
        assertEquals(originalDto.getSdpMLineIndex(), deserializedDto.getSdpMLineIndex());
        assertEquals(originalDto.getUsernameFragment(), deserializedDto.getUsernameFragment());
        assertEquals(originalDto.getFoundation(), deserializedDto.getFoundation());
        assertEquals(originalDto.getComponent(), deserializedDto.getComponent());
        assertEquals(originalDto.getProtocol(), deserializedDto.getProtocol());
        assertEquals(originalDto.getPriority(), deserializedDto.getPriority());
        assertEquals(originalDto.getIp(), deserializedDto.getIp());
        assertEquals(originalDto.getPort(), deserializedDto.getPort());
        assertEquals(originalDto.getType(), deserializedDto.getType());
        assertEquals(originalDto.getNetworkType(), deserializedDto.getNetworkType());
        assertEquals(originalDto.getNetworkCost(), deserializedDto.getNetworkCost());
        assertEquals(originalDto.getAddressFamily(), deserializedDto.getAddressFamily());
        assertEquals(originalDto.getBandwidth(), deserializedDto.getBandwidth());
        assertEquals(originalDto.getLatency(), deserializedDto.getLatency());
        assertEquals(originalDto.getPacketLoss(), deserializedDto.getPacketLoss());
        assertEquals(originalDto.getJitter(), deserializedDto.getJitter());
        assertEquals(originalDto.isEncrypted(), deserializedDto.isEncrypted());
        assertEquals(originalDto.getEncryptionType(), deserializedDto.getEncryptionType());
        assertEquals(originalDto.getCertificate(), deserializedDto.getCertificate());
        assertEquals(originalDto.getTimestamp(), deserializedDto.getTimestamp());
        assertEquals(originalDto.getErrorCode(), deserializedDto.getErrorCode());
        assertEquals(originalDto.getErrorMessage(), deserializedDto.getErrorMessage());
        assertEquals(originalDto.isHasError(), deserializedDto.isHasError());
    }

    @Test
    void WebRTCSignalingDto_ShouldHandleNullValues() throws Exception {
        // Arrange
        WebRTCSignalingDto dto = new WebRTCSignalingDto();
        // Only set a few fields, leave others null
        dto.setSessionId("test_session");
        dto.setSessionType("video");

        // Act
        String json = objectMapper.writeValueAsString(dto);
        WebRTCSignalingDto deserializedDto = objectMapper.readValue(json, WebRTCSignalingDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals("test_session", deserializedDto.getSessionId());
        assertEquals("video", deserializedDto.getSessionType());
        assertNull(deserializedDto.getRoomId());
        assertNull(deserializedDto.getPeerId());
        assertNull(deserializedDto.getSessionStatus());
        assertNull(deserializedDto.getTimestamp());
    }

    @Test
    void WebRTCSessionDescriptionDto_ShouldHandleNullValues() throws Exception {
        // Arrange
        WebRTCSessionDescriptionDto dto = new WebRTCSessionDescriptionDto();
        // Only set a few fields, leave others null
        dto.setSessionId("test_session");
        dto.setType("offer");

        // Act
        String json = objectMapper.writeValueAsString(dto);
        WebRTCSessionDescriptionDto deserializedDto = objectMapper.readValue(json, WebRTCSessionDescriptionDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals("test_session", deserializedDto.getSessionId());
        assertEquals("offer", deserializedDto.getType());
        assertNull(deserializedDto.getSdp());
        assertNull(deserializedDto.getSessionType());
        assertNull(deserializedDto.getSessionStatus());
        assertNull(deserializedDto.getTimestamp());
    }

    @Test
    void WebRTCIceCandidateDto_ShouldHandleNullValues() throws Exception {
        // Arrange
        WebRTCIceCandidateDto dto = new WebRTCIceCandidateDto();
        // Only set a few fields, leave others null
        dto.setSessionId("test_session");
        dto.setCandidate("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host");

        // Act
        String json = objectMapper.writeValueAsString(dto);
        WebRTCIceCandidateDto deserializedDto = objectMapper.readValue(json, WebRTCIceCandidateDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals("test_session", deserializedDto.getSessionId());
        assertEquals("candidate:1 1 UDP 2122252543 192.168.1.1 12345 typ host", deserializedDto.getCandidate());
        assertNull(deserializedDto.getSdpMid());
        assertNull(deserializedDto.getSdpMLineIndex());
        assertNull(deserializedDto.getUsernameFragment());
        assertNull(deserializedDto.getFoundation());
    }

    @Test
    void WebRTCSignalingDto_ShouldHandleEmptyLists() throws Exception {
        // Arrange
        WebRTCSignalingDto dto = new WebRTCSignalingDto();
        dto.setSessionId("test_session");
        dto.setStunServers(Arrays.asList());
        dto.setIceCandidates(Arrays.asList());

        // Act
        String json = objectMapper.writeValueAsString(dto);
        WebRTCSignalingDto deserializedDto = objectMapper.readValue(json, WebRTCSignalingDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals("test_session", deserializedDto.getSessionId());
        assertNotNull(deserializedDto.getStunServers());
        assertTrue(deserializedDto.getStunServers().isEmpty());
        assertNotNull(deserializedDto.getIceCandidates());
        assertTrue(deserializedDto.getIceCandidates().isEmpty());
    }

    @Test
    void WebRTCSignalingDto_ShouldHandleBooleanValues() throws Exception {
        // Arrange
        WebRTCSignalingDto dto = new WebRTCSignalingDto();
        dto.setSessionId("test_session");
        dto.setEncrypted(true);
        dto.setHasError(false);

        // Act
        String json = objectMapper.writeValueAsString(dto);
        WebRTCSignalingDto deserializedDto = objectMapper.readValue(json, WebRTCSignalingDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals("test_session", deserializedDto.getSessionId());
        assertTrue(deserializedDto.isEncrypted());
        assertFalse(deserializedDto.isHasError());
    }

    @Test
    void WebRTCIceCandidateDto_ShouldHandleIntegerValues() throws Exception {
        // Arrange
        WebRTCIceCandidateDto dto = new WebRTCIceCandidateDto();
        dto.setSessionId("test_session");
        dto.setSdpMLineIndex(0);
        dto.setPort(12345);
        dto.setTimestamp(1234567890L);

        // Act
        String json = objectMapper.writeValueAsString(dto);
        WebRTCIceCandidateDto deserializedDto = objectMapper.readValue(json, WebRTCIceCandidateDto.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserializedDto);
        assertEquals("test_session", deserializedDto.getSessionId());
        assertEquals(0, deserializedDto.getSdpMLineIndex());
        assertEquals(12345, deserializedDto.getPort());
        assertEquals(1234567890L, deserializedDto.getTimestamp());
    }
} 