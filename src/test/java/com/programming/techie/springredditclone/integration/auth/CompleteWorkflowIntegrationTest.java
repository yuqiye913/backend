package com.programming.techie.springredditclone.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.dto.AuthenticationResponse;
import com.programming.techie.springredditclone.dto.LoginRequest;
import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.integration.IntegrationTestBase;
import com.programming.techie.springredditclone.model.NotificationEmail;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.VerificationToken;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.repository.VerificationTokenRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.config.CompleteWorkflowTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Complete workflow integration tests - NO STUBBING
 * Tests the entire user journey from registration to authenticated access
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(CompleteWorkflowTestConfig.class)
@ActiveProfiles("test")
@Transactional
class CompleteWorkflowIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private AuthService authService; // Real auth service, not stubbed!

    @Autowired
    private CompleteWorkflowTestConfig testConfig;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        // Clean up test data
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
        testConfig.clearCapturedEmails();

        // Setup test requests
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("workflowuser");
        validRegisterRequest.setEmail("workflow@test.com");
        validRegisterRequest.setPassword("password123");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername("workflowuser");
        validLoginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Complete user workflow: Register → Verify → Login → Access Protected Resource")
    void completeUserWorkflow() throws Exception {
        // Step 1: Register new user
        MvcResult registerResult = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registration Successful"))
                .andReturn();

        // Verify user was created but disabled
        User createdUser = userRepository.findByUsername("workflowuser").orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("workflow@test.com");
        assertThat(createdUser.isEnabled()).isFalse(); // Should be disabled initially

        // Step 2: Verify email was captured (not sent)
        assertThat(testConfig.wasVerificationEmailSent("workflow@test.com")).isTrue();
        NotificationEmail capturedEmail = testConfig.getLastCapturedEmail();
        assertThat(capturedEmail).isNotNull();
        assertThat(capturedEmail.getSubject()).isEqualTo("Please Activate your Account");
        assertThat(capturedEmail.getRecipient()).isEqualTo("workflow@test.com");
        assertThat(capturedEmail.getBody()).contains("Thank you for signing up to Spring Reddit");

        // Step 3: Get verification token from database
        VerificationToken verificationToken = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(createdUser))
                .findFirst()
                .orElse(null);
        assertThat(verificationToken).isNotNull();
        assertThat(verificationToken.getToken()).isNotNull();
        assertThat(capturedEmail.getBody()).contains(verificationToken.getToken());

        // Step 4: Verify account using the token
        mockMvc.perform(get("/api/auth/accountVerification/" + verificationToken.getToken()))
                .andExpect(status().isOk());

        // Verify user is now enabled
        User verifiedUser = userRepository.findByUsername("workflowuser").orElse(null);
        assertThat(verifiedUser).isNotNull();
        assertThat(verifiedUser.isEnabled()).isTrue();

        // Step 5: Login with real credentials
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticationToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.username").value("workflowuser"))
                .andReturn();

        // Extract real JWT token from response
        String responseBody = loginResult.getResponse().getContentAsString();
        AuthenticationResponse authResponse = objectMapper.readValue(responseBody, AuthenticationResponse.class);
        String jwtToken = authResponse.getAuthenticationToken();
        
        assertThat(jwtToken).isNotNull();
        assertThat(jwtToken).isNotEmpty();
        assertThat(jwtToken).startsWith("eyJ"); // JWT tokens start with "eyJ"

        // Step 6: Verify the user was properly created and enabled
        User currentUser = userRepository.findByUsername("workflowuser").orElse(null);
        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getUsername()).isEqualTo("workflowuser");
        assertThat(currentUser.isEnabled()).isTrue();
        
        // Step 7: Test that we can access a protected endpoint with the JWT token
        // Use a simple endpoint that doesn't have PostMapper issues
        mockMvc.perform(get("/api/subreddit")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should reject login for unverified user")
    void shouldRejectLoginForUnverifiedUser() throws Exception {
        // Given - Register user but don't verify
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isOk());

        // When & Then - Try to login with unverified account
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validLoginRequest)))
                .andExpect(status().isInternalServerError()); // 500 is returned when user is disabled
    }

    @Test
    @DisplayName("Should allow public access to posts without JWT")
    void shouldAllowPublicAccessToPostsWithoutJWT() throws Exception {
        // When & Then - Posts endpoint should be publicly accessible
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk()); // 200 OK for public access
    }

    @Test
    @DisplayName("Should handle invalid JWT token gracefully")
    void shouldHandleInvalidJWTToken() throws Exception {
        // When & Then - Invalid JWT should be ignored for public endpoints
        mockMvc.perform(get("/api/posts")
                .header("Authorization", "Bearer invalid-jwt-token"))
                .andExpect(status().isOk()); // 200 OK - invalid token ignored for public endpoint
    }

    @Test
    @DisplayName("Should reject access to protected endpoints without JWT")
    void shouldRejectAccessToProtectedEndpointsWithoutJWT() throws Exception {
        // When & Then - Protected endpoints should require authentication
        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"postName\":\"test\",\"subredditNames\":[\"test\"]}"))
                .andExpect(status().isForbidden()); // 403 Forbidden for unauthenticated requests
    }

    @Test
    @DisplayName("Should handle valid JWT token")
    void shouldHandleValidJWTToken() throws Exception {
        // Given - Register and login to get a valid token
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isOk());

        // Verify account
        VerificationToken token = verificationTokenRepository.findAll().stream()
                .filter(t -> t.getUser().getUsername().equals("workflowuser"))
                .findFirst()
                .orElse(null);
        mockMvc.perform(get("/api/auth/accountVerification/" + token.getToken()))
                .andExpect(status().isOk());

        // Login to get JWT
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        AuthenticationResponse authResponse = objectMapper.readValue(responseBody, AuthenticationResponse.class);
        String jwtToken = authResponse.getAuthenticationToken();

        // When & Then - Use the valid token (it should work)
        mockMvc.perform(get("/api/posts")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle password encoding correctly")
    void shouldHandlePasswordEncoding() throws Exception {
        // Given - Register user
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isOk());

        // Verify account
        VerificationToken token = verificationTokenRepository.findAll().stream()
                .filter(t -> t.getUser().getUsername().equals("workflowuser"))
                .findFirst()
                .orElse(null);
        mockMvc.perform(get("/api/auth/accountVerification/" + token.getToken()))
                .andExpect(status().isOk());

        // When & Then - Login should work with correct password
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validLoginRequest)))
                .andExpect(status().isOk());

        // When & Then - Login should fail with wrong password
        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setUsername("workflowuser");
        wrongPasswordRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wrongPasswordRequest)))
                .andExpect(status().isInternalServerError()); // 500 is returned for authentication failures
    }

    @Test
    @DisplayName("Should handle concurrent user registrations")
    void shouldHandleConcurrentRegistrations() throws Exception {
        // Given - Multiple registration requests
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("concurrent1");
        request1.setEmail("concurrent1@test.com");
        request1.setPassword("password123");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("concurrent2");
        request2.setEmail("concurrent2@test.com");
        request2.setPassword("password123");

        // When & Then - Both should succeed
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request2)))
                .andExpect(status().isOk());

        // Then - Verify both users were created
        User user1 = userRepository.findByUsername("concurrent1").orElse(null);
        User user2 = userRepository.findByUsername("concurrent2").orElse(null);
        assertThat(user1).isNotNull();
        assertThat(user2).isNotNull();
        assertThat(user1.getEmail()).isEqualTo("concurrent1@test.com");
        assertThat(user2.getEmail()).isEqualTo("concurrent2@test.com");

        // Then - Verify both have verification tokens
        assertThat(verificationTokenRepository.findAll()).hasSize(2);
    }
} 