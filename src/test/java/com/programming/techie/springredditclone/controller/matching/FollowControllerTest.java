package com.programming.techie.springredditclone.controller.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.controller.FollowController;
import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    private List<GetFollowersDto> sampleFollowers;
    private FollowerCountDto sampleFollowerCount;
    private FollowingCountDto sampleFollowingCount;

    @BeforeEach
    void setUp() {
        // Create valid follow request
        validFollowRequest = new FollowRequestDto();
        validFollowRequest.setFollowingId(2L);

        // Create invalid follow request (self-following)
        invalidFollowRequest = new FollowRequestDto();
        invalidFollowRequest.setFollowingId(1L);

        // Create sample followers
        GetFollowersDto follower1 = new GetFollowersDto(1L, "follower1", "follower1@example.com", 
            Instant.now(), true, Instant.now(), true, false, false);
        GetFollowersDto follower2 = new GetFollowersDto(2L, "follower2", "follower2@example.com", 
            Instant.now(), true, Instant.now(), true, false, false);
        sampleFollowers = Arrays.asList(follower1, follower2);

        // Create sample follower count
        sampleFollowerCount = new FollowerCountDto(1L, "testuser", 2L);
        
        // Create sample following count
        sampleFollowingCount = new FollowingCountDto(1L, "testuser", 3L);
    }

    // ========== FOLLOW USER TESTS ==========
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

    // ========== UNFOLLOW USER TESTS ==========
    @Test
    @DisplayName("Should successfully unfollow a user")
    @WithMockUser(username = "testuser")
    void shouldUnfollowUser() throws Exception {
        // Given
        doNothing().when(followService).unfollowUser(eq(2L));

        // When & Then
        mockMvc.perform(delete("/api/follow/follow/2"))
                .andExpect(status().isOk());

        verify(followService).unfollowUser(2L);
    }

    @Test
    @DisplayName("Should handle service exception when unfollowing")
    @WithMockUser(username = "testuser")
    void shouldHandleServiceExceptionWhenUnfollowing() throws Exception {
        // Given
        doThrow(new RuntimeException("You are not following this user"))
                .when(followService).unfollowUser(eq(999L));

        // When & Then
        mockMvc.perform(delete("/api/follow/follow/999"))
                .andExpect(status().isInternalServerError());

        verify(followService).unfollowUser(999L);
    }

    @Test
    @DisplayName("Should return error for invalid user ID when unfollowing")
    @WithMockUser(username = "testuser")
    void shouldReturnErrorForInvalidUserIdWhenUnfollowing() throws Exception {
        // Given
        doThrow(new RuntimeException("User to unfollow not found"))
                .when(followService).unfollowUser(eq(999L));

        // When & Then
        mockMvc.perform(delete("/api/follow/follow/999"))
                .andExpect(status().isInternalServerError());

        verify(followService).unfollowUser(999L);
    }

    // ========== GET FOLLOWERS TESTS ==========
    @Test
    @DisplayName("Should get followers by user ID")
    @WithMockUser(username = "testuser")
    void shouldGetFollowersByUserId() throws Exception {
        // Given
        when(followService.getFollowersByUserId(eq(1L))).thenReturn(sampleFollowers);

        // When & Then
        mockMvc.perform(get("/api/follow/followers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("follower1"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].username").value("follower2"));

        verify(followService).getFollowersByUserId(1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no followers")
    @WithMockUser(username = "testuser")
    void shouldReturnEmptyListWhenUserHasNoFollowers() throws Exception {
        // Given
        when(followService.getFollowersByUserId(eq(1L))).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/follow/followers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(followService).getFollowersByUserId(1L);
    }

    @Test
    @DisplayName("Should handle service exception when getting followers")
    @WithMockUser(username = "testuser")
    void shouldHandleServiceExceptionWhenGettingFollowers() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found"))
                .when(followService).getFollowersByUserId(eq(999L));

        // When & Then
        mockMvc.perform(get("/api/follow/followers/999"))
                .andExpect(status().isInternalServerError());

        verify(followService).getFollowersByUserId(999L);
    }

    // ========== GET FOLLOWER COUNT TESTS ==========
    @Test
    @DisplayName("Should get follower count by user ID")
    @WithMockUser(username = "testuser")
    void shouldGetFollowerCountByUserId() throws Exception {
        // Given
        when(followService.getFollowerCountByUserId(eq(1L))).thenReturn(sampleFollowerCount);

        // When & Then
        mockMvc.perform(get("/api/follow/followers/count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.followerCount").value(2));

        verify(followService).getFollowerCountByUserId(1L);
    }

    @Test
    @DisplayName("Should return zero count when user has no followers")
    @WithMockUser(username = "testuser")
    void shouldReturnZeroCountWhenUserHasNoFollowers() throws Exception {
        // Given
        FollowerCountDto zeroCount = new FollowerCountDto(1L, "testuser", 0L);
        when(followService.getFollowerCountByUserId(eq(1L))).thenReturn(zeroCount);

        // When & Then
        mockMvc.perform(get("/api/follow/followers/count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followerCount").value(0));

        verify(followService).getFollowerCountByUserId(1L);
    }

    @Test
    @DisplayName("Should handle service exception when getting follower count")
    @WithMockUser(username = "testuser")
    void shouldHandleServiceExceptionWhenGettingFollowerCount() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found"))
                .when(followService).getFollowerCountByUserId(eq(999L));

        // When & Then
        mockMvc.perform(get("/api/follow/followers/count/999"))
                .andExpect(status().isInternalServerError());

        verify(followService).getFollowerCountByUserId(999L);
    }

    // ========== GET FOLLOWING COUNT TESTS ==========
    @Test
    @DisplayName("Should get following count by user ID")
    @WithMockUser(username = "testuser")
    void shouldGetFollowingCountByUserId() throws Exception {
        // Given
        when(followService.getFollowingCountByUserId(eq(1L))).thenReturn(sampleFollowingCount);

        // When & Then
        mockMvc.perform(get("/api/follow/following/count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.followingCount").value(3));

        verify(followService).getFollowingCountByUserId(1L);
    }

    @Test
    @DisplayName("Should return zero count when user is not following anyone")
    @WithMockUser(username = "testuser")
    void shouldReturnZeroCountWhenUserIsNotFollowingAnyone() throws Exception {
        // Given
        FollowingCountDto zeroCount = new FollowingCountDto(1L, "testuser", 0L);
        when(followService.getFollowingCountByUserId(eq(1L))).thenReturn(zeroCount);

        // When & Then
        mockMvc.perform(get("/api/follow/following/count/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followingCount").value(0));

        verify(followService).getFollowingCountByUserId(1L);
    }

    @Test
    @DisplayName("Should handle service exception when getting following count")
    @WithMockUser(username = "testuser")
    void shouldHandleServiceExceptionWhenGettingFollowingCount() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found"))
                .when(followService).getFollowingCountByUserId(eq(999L));

        // When & Then
        mockMvc.perform(get("/api/follow/following/count/999"))
                .andExpect(status().isInternalServerError());

        verify(followService).getFollowingCountByUserId(999L);
    }

    // ========== AUTHENTICATION TESTS ==========
    @Test
    @DisplayName("Should require authentication for follow endpoint")
    void shouldRequireAuthenticationForFollowEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validFollowRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require authentication for unfollow endpoint")
    void shouldRequireAuthenticationForUnfollowEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/follow/follow/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require authentication for get followers endpoint")
    void shouldRequireAuthenticationForGetFollowersEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/follow/followers/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require authentication for get follower count endpoint")
    void shouldRequireAuthenticationForGetFollowerCountEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/follow/followers/count/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require authentication for get following count endpoint")
    void shouldRequireAuthenticationForGetFollowingCountEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/follow/following/count/1"))
                .andExpect(status().isForbidden());
    }
} 