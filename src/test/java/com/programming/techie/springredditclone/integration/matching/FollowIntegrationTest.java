package com.programming.techie.springredditclone.integration.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.LoginRequest;
import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.FollowRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class FollowIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private User user1;
    private User user2;
    private User user3;
    private String user1Token;
    private String user2Token;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test users
        user1 = createUser("user1", "user1@example.com", "password");
        user2 = createUser("user2", "user2@example.com", "password");
        user3 = createUser("user3", "user3@example.com", "password");

        // Get authentication tokens
        user1Token = getAuthToken("user1", "password");
        user2Token = getAuthToken("user2", "password");
    }

    private User createUser(String username, String email, String password) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        return userRepository.findByUsername(username).orElseThrow();
    }

    private String getAuthToken(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("authenticationToken").asText();
    }

    // ========== FOLLOW USER INTEGRATION TESTS ==========
    @Test
    @DisplayName("Should successfully follow a user")
    void shouldFollowUser() throws Exception {
        // Given
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user2.getUserId());

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk());

        // Verify follow relationship was created
        assertThat(followRepository.existsByFollowerAndFollowing(user1, user2)).isTrue();
    }

    @Test
    @DisplayName("Should not allow self-following")
    void shouldNotAllowSelfFollowing() throws Exception {
        // Given
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user1.getUserId());

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isInternalServerError());

        // Verify no follow relationship was created
        assertThat(followRepository.existsByFollowerAndFollowing(user1, user1)).isFalse();
    }

    @Test
    @DisplayName("Should not allow following the same user twice")
    void shouldNotAllowFollowingSameUserTwice() throws Exception {
        // Given
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user2.getUserId());

        // First follow
        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk());

        // Second follow attempt
        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isInternalServerError());

        // Verify only one follow relationship exists
        assertThat(followRepository.findByFollowerAndFollowing(user1, user2)).isPresent();
    }

    // ========== UNFOLLOW USER INTEGRATION TESTS ==========
    @Test
    @DisplayName("Should successfully unfollow a user")
    void shouldUnfollowUser() throws Exception {
        // Given - First follow the user
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user2.getUserId());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk());

        // When - Unfollow the user
        mockMvc.perform(delete("/api/follow/follow/" + user2.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk());

        // Then - Verify follow relationship was removed
        assertThat(followRepository.existsByFollowerAndFollowing(user1, user2)).isFalse();
    }

    @Test
    @DisplayName("Should not allow unfollowing when not following")
    void shouldNotAllowUnfollowingWhenNotFollowing() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/follow/follow/" + user2.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isInternalServerError());

        // Verify no follow relationship exists
        assertThat(followRepository.existsByFollowerAndFollowing(user1, user2)).isFalse();
    }

    // ========== IS FOLLOWING USER INTEGRATION TESTS ==========
    @Test
    @DisplayName("Should return true when user is following another user")
    void shouldReturnTrueWhenUserIsFollowing() throws Exception {
        // Given - First follow the user
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user2.getUserId());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk());

        // When & Then - Check follow status
        mockMvc.perform(get("/api/follow/is-following/" + user2.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("Should return false when user is not following another user")
    void shouldReturnFalseWhenUserIsNotFollowing() throws Exception {
        // When & Then - Check follow status
        mockMvc.perform(get("/api/follow/is-following/" + user2.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("Should return false when checking follow status for non-existent user")
    void shouldReturnFalseWhenCheckingFollowStatusForNonExistentUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/follow/is-following/999999")
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle multiple follow status checks correctly")
    void shouldHandleMultipleFollowStatusChecks() throws Exception {
        // Given - Follow user2 but not user3
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user2.getUserId());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk());

        // When & Then - Check follow status for both users
        mockMvc.perform(get("/api/follow/is-following/" + user2.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/api/follow/is-following/" + user3.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("Should handle follow status after unfollowing")
    void shouldHandleFollowStatusAfterUnfollowing() throws Exception {
        // Given - Follow then unfollow user2
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user2.getUserId());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/follow/follow/" + user2.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk());

        // When & Then - Check follow status should be false
        mockMvc.perform(get("/api/follow/is-following/" + user2.getUserId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ========== AUTHENTICATION INTEGRATION TESTS ==========
    @Test
    @DisplayName("Should require authentication for follow endpoint")
    void shouldRequireAuthenticationForFollowEndpoint() throws Exception {
        // Given
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user2.getUserId());

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require authentication for unfollow endpoint")
    void shouldRequireAuthenticationForUnfollowEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/follow/follow/" + user2.getUserId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require authentication for is-following endpoint")
    void shouldRequireAuthenticationForIsFollowingEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/follow/is-following/" + user2.getUserId()))
                .andExpect(status().isForbidden());
    }

    // ========== FOLLOWER COUNT INTEGRATION TESTS ==========
    @Test
    @DisplayName("Should get correct follower count")
    void shouldGetCorrectFollowerCount() throws Exception {
        // Given - user1 and user3 follow user2
        FollowRequestDto followRequest1 = new FollowRequestDto();
        followRequest1.setFollowingId(user2.getUserId());

        FollowRequestDto followRequest2 = new FollowRequestDto();
        followRequest2.setFollowingId(user2.getUserId());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + getAuthToken("user3", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest2)))
                .andExpect(status().isOk());

        // When & Then - Check follower count
        mockMvc.perform(get("/api/follow/followers/count/" + user2.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followerCount").value(2))
                .andExpect(jsonPath("$.userId").value(user2.getUserId()))
                .andExpect(jsonPath("$.username").value(user2.getUsername()));
    }

    // ========== FOLLOWING COUNT INTEGRATION TESTS ==========
    @Test
    @DisplayName("Should get correct following count")
    void shouldGetCorrectFollowingCount() throws Exception {
        // Given - user1 follows user2 and user3
        FollowRequestDto followRequest1 = new FollowRequestDto();
        followRequest1.setFollowingId(user2.getUserId());

        FollowRequestDto followRequest2 = new FollowRequestDto();
        followRequest2.setFollowingId(user3.getUserId());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest2)))
                .andExpect(status().isOk());

        // When & Then - Check following count
        mockMvc.perform(get("/api/follow/following/count/" + user1.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followingCount").value(2))
                .andExpect(jsonPath("$.userId").value(user1.getUserId()))
                .andExpect(jsonPath("$.username").value(user1.getUsername()));
    }

    // ========== EDGE CASES ==========
    @Test
    @DisplayName("Should handle invalid user ID in follow request")
    void shouldHandleInvalidUserIdInFollowRequest() throws Exception {
        // Given
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(999999L); // Non-existent user

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid user ID in unfollow request")
    void shouldHandleInvalidUserIdInUnfollowRequest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/follow/follow/999999")
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid user ID in is-following request")
    void shouldHandleInvalidUserIdInIsFollowingRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/follow/is-following/999999")
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle malformed JSON in follow request")
    void shouldHandleMalformedJsonInFollowRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing following ID in follow request")
    void shouldHandleMissingFollowingIdInFollowRequest() throws Exception {
        // Given
        FollowRequestDto followRequest = new FollowRequestDto();
        // followingId is null

        // When & Then
        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequest)))
                .andExpect(status().isBadRequest());
    }
} 