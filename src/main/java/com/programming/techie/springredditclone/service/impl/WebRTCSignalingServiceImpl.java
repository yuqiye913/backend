package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.dto.WebRTCSignalingDto;
import com.programming.techie.springredditclone.dto.WebRTCIceCandidateDto;
import com.programming.techie.springredditclone.dto.WebRTCSessionDescriptionDto;
import com.programming.techie.springredditclone.service.WebRTCSignalingService;
import com.programming.techie.springredditclone.model.VideoCallSession;
import com.programming.techie.springredditclone.repository.VideoCallSessionRepository;
import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class WebRTCSignalingServiceImpl implements WebRTCSignalingService {

    private final VideoCallSessionRepository videoCallSessionRepository;
    
    // In-memory storage for real-time signaling (in production, use Redis or similar)
    private final Map<String, List<WebRTCIceCandidateDto>> iceCandidatesMap = new ConcurrentHashMap<>();
    private final Map<String, WebRTCSessionDescriptionDto> sessionDescriptionsMap = new ConcurrentHashMap<>();
    private final Map<String, WebRTCSignalingDto> sessionsMap = new ConcurrentHashMap<>();

    @Override
    public WebRTCSignalingDto createSession(String sessionId) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        WebRTCSignalingDto signalingDto = new WebRTCSignalingDto();
        signalingDto.setSessionId(sessionId);
        signalingDto.setRoomId(session.getRoomId());
        signalingDto.setPeerId(session.getPeerId());
        signalingDto.setSessionType("video");
        signalingDto.setSessionStatus("active");
        signalingDto.setTimestamp(Instant.now().toEpochMilli());
        
        // Configure STUN/TURN servers
        List<String> stunServers = Arrays.asList(
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302"
        );
        signalingDto.setStunServers(stunServers);
        
        // Configure ICE servers JSON
        String iceServersJson = createIceServersJson(stunServers);
        signalingDto.setIceServers(iceServersJson);
        
        // Configure RTC configuration
        String rtcConfigJson = createRTCConfigurationJson();
        signalingDto.setRtcConfiguration(rtcConfigJson);
        
        // Configure media constraints
        String constraintsJson = createMediaConstraintsJson();
        signalingDto.setConstraints(constraintsJson);
        
        // Set video/audio codecs
        signalingDto.setVideoCodec("H.264");
        signalingDto.setAudioCodec("Opus");
        signalingDto.setVideoResolution("1280x720");
        signalingDto.setVideoFrameRate("30fps");
        signalingDto.setAudioSampleRate("48kHz");
        signalingDto.setAudioChannels("stereo");
        
        // Security settings
        signalingDto.setEncrypted(true);
        signalingDto.setEncryptionType("DTLS");
        
        sessionsMap.put(sessionId, signalingDto);
        iceCandidatesMap.put(sessionId, new ArrayList<>());
        
        log.info("Created WebRTC signaling session: {}", sessionId);
        return signalingDto;
    }

    @Override
    public WebRTCSessionDescriptionDto generateOffer(String sessionId) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        WebRTCSessionDescriptionDto offer = new WebRTCSessionDescriptionDto();
        offer.setSessionId(sessionId);
        offer.setType("offer");
        offer.setSessionType("video");
        offer.setSessionStatus("connecting");
        offer.setTimestamp(Instant.now().toEpochMilli());
        
        // Generate a sample SDP offer (in real implementation, this would come from the client)
        String sdpOffer = generateSampleSDPOffer();
        offer.setSdp(sdpOffer);
        
        // Set media information
        offer.setVideoCodec("H.264");
        offer.setAudioCodec("Opus");
        offer.setVideoResolution("1280x720");
        offer.setVideoFrameRate("30fps");
        offer.setAudioSampleRate("48kHz");
        offer.setAudioChannels("stereo");
        
        // Set quality settings
        offer.setVideoBitrate("1000");
        offer.setAudioBitrate("64");
        offer.setVideoQuality("high");
        offer.setAudioQuality("high");
        
        // Set network settings
        offer.setNetworkType("wifi");
        offer.setBandwidth("1000");
        offer.setLatency("50");
        
        // Set security settings
        offer.setEncrypted(true);
        offer.setEncryptionType("DTLS");
        
        // Set ICE settings
        offer.setIceServers(createIceServersJson(Arrays.asList("stun:stun.l.google.com:19302")));
        offer.setIceTransportPolicy("all");
        offer.setBundlePolicy("max-bundle");
        offer.setRtcpMuxPolicy("require");
        
        sessionDescriptionsMap.put(sessionId + "_offer", offer);
        
        log.info("Generated offer for session: {}", sessionId);
        return offer;
    }

    @Override
    public WebRTCSessionDescriptionDto generateAnswer(String sessionId, String offerSdp) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        WebRTCSessionDescriptionDto answer = new WebRTCSessionDescriptionDto();
        answer.setSessionId(sessionId);
        answer.setType("answer");
        answer.setSessionType("video");
        answer.setSessionStatus("connecting");
        answer.setTimestamp(Instant.now().toEpochMilli());
        
        // Generate a sample SDP answer (in real implementation, this would come from the client)
        String sdpAnswer = generateSampleSDPAnswer();
        answer.setSdp(sdpAnswer);
        
        // Set media information (should match offer)
        answer.setVideoCodec("H.264");
        answer.setAudioCodec("Opus");
        answer.setVideoResolution("1280x720");
        answer.setVideoFrameRate("30fps");
        answer.setAudioSampleRate("48kHz");
        answer.setAudioChannels("stereo");
        
        // Set quality settings
        answer.setVideoBitrate("1000");
        answer.setAudioBitrate("64");
        answer.setVideoQuality("high");
        answer.setAudioQuality("high");
        
        // Set network settings
        answer.setNetworkType("wifi");
        answer.setBandwidth("1000");
        answer.setLatency("50");
        
        // Set security settings
        answer.setEncrypted(true);
        answer.setEncryptionType("DTLS");
        
        // Set ICE settings
        answer.setIceServers(createIceServersJson(Arrays.asList("stun:stun.l.google.com:19302")));
        answer.setIceTransportPolicy("all");
        answer.setBundlePolicy("max-bundle");
        answer.setRtcpMuxPolicy("require");
        
        sessionDescriptionsMap.put(sessionId + "_answer", answer);
        
        log.info("Generated answer for session: {}", sessionId);
        return answer;
    }

    @Override
    public void addIceCandidate(String sessionId, WebRTCIceCandidateDto candidate) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        candidate.setSessionId(sessionId);
        candidate.setTimestamp(Instant.now().toEpochMilli());
        
        // Add candidate to the session's candidate list
        iceCandidatesMap.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(candidate);
        
        log.info("Added ICE candidate for session: {}, candidate: {}", sessionId, candidate.getCandidate());
    }

    @Override
    public List<WebRTCIceCandidateDto> getIceCandidates(String sessionId) {
        return iceCandidatesMap.getOrDefault(sessionId, new ArrayList<>());
    }

    @Override
    public void updateSessionDescription(String sessionId, WebRTCSessionDescriptionDto description) {
        VideoCallSession session = videoCallSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SpringRedditException("Video call session not found"));

        description.setSessionId(sessionId);
        description.setTimestamp(Instant.now().toEpochMilli());
        
        String key = sessionId + "_" + description.getType();
        sessionDescriptionsMap.put(key, description);
        
        log.info("Updated session description for session: {}, type: {}", sessionId, description.getType());
    }

    @Override
    public WebRTCSessionDescriptionDto getSessionDescription(String sessionId) {
        // Return the most recent session description (offer or answer)
        WebRTCSessionDescriptionDto offer = sessionDescriptionsMap.get(sessionId + "_offer");
        WebRTCSessionDescriptionDto answer = sessionDescriptionsMap.get(sessionId + "_answer");
        
        if (answer != null) return answer;
        if (offer != null) return offer;
        return null;
    }

    @Override
    public void closeSession(String sessionId) {
        sessionsMap.remove(sessionId);
        iceCandidatesMap.remove(sessionId);
        sessionDescriptionsMap.entrySet().removeIf(entry -> entry.getKey().startsWith(sessionId + "_"));
        
        log.info("Closed WebRTC signaling session: {}", sessionId);
    }

    @Override
    public boolean isSessionActive(String sessionId) {
        return sessionsMap.containsKey(sessionId);
    }

    @Override
    public WebRTCSignalingDto getServerConfiguration() {
        WebRTCSignalingDto config = new WebRTCSignalingDto();
        config.setSessionId("server_config");
        config.setSessionType("configuration");
        config.setSessionStatus("active");
        config.setTimestamp(Instant.now().toEpochMilli());
        
        // Configure STUN/TURN servers
        List<String> stunServers = Arrays.asList(
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302"
        );
        config.setStunServers(stunServers);
        config.setIceServers(createIceServersJson(stunServers));
        config.setRtcConfiguration(createRTCConfigurationJson());
        config.setConstraints(createMediaConstraintsJson());
        
        return config;
    }

    // Helper methods for generating JSON configurations
    private String createIceServersJson(List<String> stunServers) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < stunServers.size(); i++) {
            if (i > 0) json.append(",");
            json.append("{\"urls\":\"").append(stunServers.get(i)).append("\"}");
        }
        json.append("]");
        return json.toString();
    }

    private String createRTCConfigurationJson() {
        return "{" +
                "\"iceServers\":[" +
                "{\"urls\":\"stun:stun.l.google.com:19302\"}," +
                "{\"urls\":\"stun:stun1.l.google.com:19302\"}" +
                "]," +
                "\"iceCandidatePoolSize\":10," +
                "\"bundlePolicy\":\"max-bundle\"," +
                "\"rtcpMuxPolicy\":\"require\"," +
                "\"iceTransportPolicy\":\"all\"" +
                "}";
    }

    private String createMediaConstraintsJson() {
        return "{" +
                "\"video\":{" +
                "\"width\":{\"ideal\":1280}," +
                "\"height\":{\"ideal\":720}," +
                "\"frameRate\":{\"ideal\":30}" +
                "}," +
                "\"audio\":{" +
                "\"sampleRate\":{\"ideal\":48000}," +
                "\"channelCount\":{\"ideal\":2}" +
                "}" +
                "}";
    }

    private String generateSampleSDPOffer() {
        return "v=0\r\n" +
                "o=- 1234567890 2 IN IP4 127.0.0.1\r\n" +
                "s=-\r\n" +
                "t=0 0\r\n" +
                "a=group:BUNDLE 0 1\r\n" +
                "a=msid-semantic: WMS\r\n" +
                "m=audio 9 UDP/TLS/RTP/SAVPF 111\r\n" +
                "c=IN IP4 0.0.0.0\r\n" +
                "a=mid:0\r\n" +
                "a=sendonly\r\n" +
                "a=rtpmap:111 opus/48000/2\r\n" +
                "m=video 9 UDP/TLS/RTP/SAVPF 96\r\n" +
                "c=IN IP4 0.0.0.0\r\n" +
                "a=mid:1\r\n" +
                "a=sendonly\r\n" +
                "a=rtpmap:96 H264/90000\r\n";
    }

    private String generateSampleSDPAnswer() {
        return "v=0\r\n" +
                "o=- 1234567890 2 IN IP4 127.0.0.1\r\n" +
                "s=-\r\n" +
                "t=0 0\r\n" +
                "a=group:BUNDLE 0 1\r\n" +
                "a=msid-semantic: WMS\r\n" +
                "m=audio 9 UDP/TLS/RTP/SAVPF 111\r\n" +
                "c=IN IP4 0.0.0.0\r\n" +
                "a=mid:0\r\n" +
                "a=recvonly\r\n" +
                "a=rtpmap:111 opus/48000/2\r\n" +
                "m=video 9 UDP/TLS/RTP/SAVPF 96\r\n" +
                "c=IN IP4 0.0.0.0\r\n" +
                "a=mid:1\r\n" +
                "a=recvonly\r\n" +
                "a=rtpmap:96 H264/90000\r\n";
    }
} 