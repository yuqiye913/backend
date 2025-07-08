package com.programming.techie.springredditclone.controller.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.controller.FollowController;
import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.programming.techie.springredditclone.config.TestSecurityConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowController.class)
@Import(TestSecurityConfig.class)
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FollowService followService;

    @Autowired
    private ObjectMapper objectMapper;

    private FollowRequestDto validFollowRequest;
    private FollowRequestDto invalidFollowRequest;

    @BeforeEach
    void setUp() {
        // Create valid follow request
        validFollowRequest = new FollowRequestDto();
        validFollowRequest.setFollowingId(2L);

        // Create invalid follow request (self-following)
        invalidFollowRequest = new FollowRequestDto();
        invalidFollowRequest.setFollowingId(1L);
    }

    @Test
    @DisplayName("Should successfully follow a user")
    @WithMockUser(username = "testuser")
    void shouldFollowUser() throws Exception {
        // Given
        doNothing().when(followService).followUser(any(FollowRequestDto.class));

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validFollowRequest)))
                .andExpect(status().isOk());

        verify(followService).followUser(any(FollowRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when following ID is null")
    @WithMockUser(username = "testuser")
    void shouldReturnBadRequestWhenFollowingIdIsNull() throws Exception {
        // Given
        FollowRequestDto request = new FollowRequestDto();
        request.setFollowingId(null);

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    @WithMockUser(username = "testuser")
    void shouldHandleServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found"))
                .when(followService).followUser(any(FollowRequestDto.class));

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validFollowRequest)))
                .andExpect(status().isInternalServerError());

        verify(followService).followUser(any(FollowRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 for invalid JSON")
    @WithMockUser(username = "testuser")
    void shouldReturnBadRequestForInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 415 for unsupported media type")
    @WithMockUser(username = "testuser")
    void shouldReturnUnsupportedMediaType() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .contentType(MediaType.TEXT_PLAIN)
                .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }
} 