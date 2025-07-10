package com.programming.techie.springredditclone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.config.CompleteWorkflowTestConfig;
import com.programming.techie.springredditclone.dto.*;
import com.programming.techie.springredditclone.integration.IntegrationTestBase;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.VerificationToken;
import com.programming.techie.springredditclone.model.VoteType;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.repository.VerificationTokenRepository;
import com.programming.techie.springredditclone.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Complete User Journey Integration Test
 * 
 * This test simulates a realistic user experience from start to finish:
 * 1. User registration and verification
 * 2. Login and JWT authentication
 * 3. Subreddit creation
 * 4. Post creation
 * 5. Comment creation
 * 6. Voting on posts and comments
 * 7. User following
 * 8. User blocking
 * 9. Notification checking
 * 
 * This ensures all major features work together in a real-world scenario.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(CompleteWorkflowTestConfig.class)
@ActiveProfiles("test")
@Transactional
class CompleteUserJourneyIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CompleteWorkflowTestConfig testConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest user1RegisterRequest;
    private RegisterRequest user2RegisterRequest;
    private LoginRequest user1LoginRequest;
    private LoginRequest user2LoginRequest;

    @BeforeEach
    void setUp() {
        // Setup User 1 (Main user)
        user1RegisterRequest = new RegisterRequest();
        user1RegisterRequest.setUsername("journeyuser1");
        user1RegisterRequest.setEmail("journey1@test.com");
        user1RegisterRequest.setPassword("password123");

        user1LoginRequest = new LoginRequest();
        user1LoginRequest.setUsername("journeyuser1");
        user1LoginRequest.setPassword("password123");

        // Setup User 2 (Secondary user for interactions)
        user2RegisterRequest = new RegisterRequest();
        user2RegisterRequest.setUsername("journeyuser2");
        user2RegisterRequest.setEmail("journey2@test.com");
        user2RegisterRequest.setPassword("password123");

        user2LoginRequest = new LoginRequest();
        user2LoginRequest.setUsername("journeyuser2");
        user2LoginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Complete user journey: Register → Verify → Login → Create Subreddit → Post → Comment → Vote → Follow → Block → Check Notifications")
    void completeUserJourney() throws Exception {
        
        // ===== PHASE 1: USER REGISTRATION AND VERIFICATION =====
        
        // Step 1: Register User 1
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user1RegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registration Successful"));

        // Step 2: Register User 2
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user2RegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registration Successful"));

        // Step 3: Verify User 1 account
        User user1 = userRepository.findByUsername("journeyuser1").orElse(null);
        assertThat(user1).isNotNull();
        VerificationToken user1Token = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(user1))
                .findFirst()
                .orElse(null);
        assertThat(user1Token).isNotNull();

        mockMvc.perform(get("/api/auth/accountVerification/" + user1Token.getToken()))
                .andExpect(status().isOk());

        // Step 4: Verify User 2 account
        User user2 = userRepository.findByUsername("journeyuser2").orElse(null);
        assertThat(user2).isNotNull();
        VerificationToken user2Token = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(user2))
                .findFirst()
                .orElse(null);
        assertThat(user2Token).isNotNull();

        mockMvc.perform(get("/api/auth/accountVerification/" + user2Token.getToken()))
                .andExpect(status().isOk());

        // ===== PHASE 2: LOGIN AND AUTHENTICATION =====
        
        // Step 5: Login User 1 and get JWT token
        MvcResult user1LoginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user1LoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticationToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.username").value("journeyuser1"))
                .andReturn();

        String user1ResponseBody = user1LoginResult.getResponse().getContentAsString();
        AuthenticationResponse user1AuthResponse = objectMapper.readValue(user1ResponseBody, AuthenticationResponse.class);
        String user1JwtToken = user1AuthResponse.getAuthenticationToken();

        // Step 6: Login User 2 and get JWT token
        MvcResult user2LoginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user2LoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticationToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.username").value("journeyuser2"))
                .andReturn();

        String user2ResponseBody = user2LoginResult.getResponse().getContentAsString();
        AuthenticationResponse user2AuthResponse = objectMapper.readValue(user2ResponseBody, AuthenticationResponse.class);
        String user2JwtToken = user2AuthResponse.getAuthenticationToken();

        // ===== PHASE 3: SUBREDDIT CREATION =====
        
        // Step 7: User 1 creates a subreddit
        SubredditDto subredditRequest = new SubredditDto();
        subredditRequest.setName("journey-test-subreddit");
        subredditRequest.setDescription("A test subreddit for the complete user journey");

        MvcResult subredditResult = mockMvc.perform(post("/api/subreddit")
                .header("Authorization", "Bearer " + user1JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(subredditRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String subredditResponseBody = subredditResult.getResponse().getContentAsString();
        SubredditDto createdSubreddit = objectMapper.readValue(subredditResponseBody, SubredditDto.class);
        assertThat(createdSubreddit.getName()).isEqualTo("journey-test-subreddit");

        // ===== PHASE 4: POST CREATION =====
        
        // Step 8: User 1 creates a post in the subreddit
        PostRequest postRequest = new PostRequest();
        postRequest.setPostName("My First Journey Post");
        postRequest.setDescription("This is a test post created during the complete user journey");
        postRequest.setUrl("https://example.com/journey-post");
        postRequest.setSubredditNames(java.util.List.of("journey-test-subreddit"));

        MvcResult postResult = mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + user1JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // ===== PHASE 5: COMMENT CREATION =====
        
        // Step 9: User 2 comments on User 1's post
        CommentsDto commentRequest = new CommentsDto();
        commentRequest.setText("Great post! This is a test comment from the journey.");
        commentRequest.setPostId(1L); // Assuming this is the first post

        mockMvc.perform(post("/api/comments")
                .header("Authorization", "Bearer " + user2JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(commentRequest)))
                .andExpect(status().isCreated());

        // ===== PHASE 6: VOTING =====
        
        // Step 10: User 2 upvotes User 1's post
        VoteDto upvoteRequest = new VoteDto();
        upvoteRequest.setPostId(1L);
        upvoteRequest.setVoteType(VoteType.UPVOTE);

        mockMvc.perform(post("/api/votes")
                .header("Authorization", "Bearer " + user2JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(upvoteRequest)))
                .andExpect(status().isOk());

        // Step 11: User 1 upvotes the comment
        CommentVoteRequest commentUpvoteRequest = CommentVoteRequest.builder()
                .commentId(1L)
                .voteType(VoteType.UPVOTE)
                .build();

        mockMvc.perform(post("/api/votes/comment")
                .header("Authorization", "Bearer " + user1JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(commentUpvoteRequest)))
                .andExpect(status().isOk());

        // ===== PHASE 7: USER FOLLOWING =====
        
        // Step 12: User 2 follows User 1
        FollowRequestDto followRequest = new FollowRequestDto();
        followRequest.setFollowingId(user1.getUserId());

        mockMvc.perform(post("/api/follow/follow")
                .header("Authorization", "Bearer " + user2JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(followRequest)))
                .andExpect(status().isOk());

        // ===== PHASE 8: USER BLOCKING =====
        
        // Step 13: User 1 blocks User 2 (this will prevent User 2 from voting on User 1's future posts)
        BlockRequestDto blockRequest = new BlockRequestDto();
        blockRequest.setBlockedUserId(user2.getUserId());

        mockMvc.perform(post("/api/blocks")
                .header("Authorization", "Bearer " + user1JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(blockRequest)))
                .andExpect(status().isOk());

        // ===== PHASE 9: VERIFY BLOCKING EFFECTS =====
        
        // Step 14: User 2 tries to vote on User 1's post (should fail due to blocking)
        VoteDto blockedVoteRequest = new VoteDto();
        blockedVoteRequest.setPostId(1L);
        blockedVoteRequest.setVoteType(VoteType.DOWNVOTE);

        mockMvc.perform(post("/api/votes")
                .header("Authorization", "Bearer " + user2JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(blockedVoteRequest)))
                .andExpect(status().isBadRequest()); // Should fail due to blocking

        // ===== PHASE 10: NOTIFICATION CHECKING =====
        
        // Step 15: User 1 checks their notifications (should include the upvote notification)
        mockMvc.perform(get("/api/notifications")
                .header("Authorization", "Bearer " + user1JwtToken))
                .andExpect(status().isOk());

        // Step 16: User 1 checks unread notification count
        mockMvc.perform(get("/api/notifications/count/unread")
                .header("Authorization", "Bearer " + user1JwtToken))
                .andExpect(status().isOk());

        // ===== PHASE 11: DATA VERIFICATION =====
        
        // Step 17: Verify the post can be retrieved with vote status
        mockMvc.perform(get("/api/posts/1")
                .header("Authorization", "Bearer " + user1JwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteCount").exists());

        // Step 18: Verify comments can be retrieved
        mockMvc.perform(get("/api/comments?postId=1")
                .header("Authorization", "Bearer " + user1JwtToken))
                .andExpect(status().isOk());

        // Step 19: Verify subreddit can be retrieved
        mockMvc.perform(get("/api/subreddit"))
                .andExpect(status().isOk());

        // ===== PHASE 12: CLEANUP VERIFICATION =====
        
        // Step 20: User 1 unblocks User 2
        UnblockRequestDto unblockRequest = new UnblockRequestDto();
        unblockRequest.setBlockedUserId(user2.getUserId());
        mockMvc.perform(delete("/api/blocks")
                .header("Authorization", "Bearer " + user1JwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(unblockRequest)))
                .andExpect(status().isOk());

        // Step 21: User 2 unfollows User 1
        mockMvc.perform(delete("/api/follow/follow/" + user1.getUserId())
                .header("Authorization", "Bearer " + user2JwtToken))
                .andExpect(status().isOk());

        // ===== FINAL VERIFICATION =====
        
        // Verify both users are still properly authenticated
        User verifiedUser1 = userRepository.findByUsername("journeyuser1").orElse(null);
        User verifiedUser2 = userRepository.findByUsername("journeyuser2").orElse(null);
        
        assertThat(verifiedUser1).isNotNull();
        assertThat(verifiedUser1.isEnabled()).isTrue();
        assertThat(verifiedUser2).isNotNull();
        assertThat(verifiedUser2.isEnabled()).isTrue();
        
        // Verify email notifications were captured
        assertThat(testConfig.getCapturedEmails()).hasSize(2); // One for each user
    }

    // Using inherited asJsonString method from IntegrationTestBase
} 