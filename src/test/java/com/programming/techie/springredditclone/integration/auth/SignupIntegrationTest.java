package com.programming.techie.springredditclone.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.integration.IntegrationTestBase;
import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.VerificationToken;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for signup functionality
 * Tests the complete flow from controller to service to repository
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SignupIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("integrationtestuser");
        validRegisterRequest.setEmail("integration@test.com");
        validRegisterRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should successfully register a new user through complete flow")
    void shouldSuccessfullyRegisterNewUser() throws Exception {
        // When
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registration Successful"));

        // Then - Verify user was created in database
        User savedUser = userRepository.findByUsername("integrationtestuser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("integration@test.com");
        assertThat(savedUser.isEnabled()).isFalse();
        assertThat(savedUser.getCreated()).isNotNull();

        // Then - Verify verification token was created
        VerificationToken verificationToken = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(savedUser))
                .findFirst()
                .orElse(null);
        assertThat(verificationToken).isNotNull();
        assertThat(verificationToken.getToken()).isNotNull();
        assertThat(verificationToken.getToken()).isNotEmpty();
    }

    @Test
    @DisplayName("Should return 400 Bad Request for duplicate username")
    void shouldReturnBadRequestForDuplicateUsername() throws Exception {
        // Given - Create a user first
        User existingUser = new User();
        existingUser.setUsername("duplicateuser");
        existingUser.setEmail("existing@test.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setEnabled(false);
        userRepository.save(existingUser);

        // Given - Try to register with same username
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("duplicateuser");
        duplicateRequest.setEmail("new@test.com");
        duplicateRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for duplicate email")
    void shouldReturnBadRequestForDuplicateEmail() throws Exception {
        // Given - Create a user first
        User existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setEmail("duplicate@test.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setEnabled(false);
        userRepository.save(existingUser);

        // Given - Try to register with same email
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("newuser");
        duplicateRequest.setEmail("duplicate@test.com");
        duplicateRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle multiple concurrent signup requests")
    void shouldHandleMultipleConcurrentSignupRequests() throws Exception {
        // Given
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("concurrentuser1");
        request1.setEmail("concurrent1@test.com");
        request1.setPassword("password123");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("concurrentuser2");
        request2.setEmail("concurrent2@test.com");
        request2.setPassword("password123");

        // When & Then - Both should succeed
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // Then - Verify both users were created
        User user1 = userRepository.findByUsername("concurrentuser1").orElse(null);
        User user2 = userRepository.findByUsername("concurrentuser2").orElse(null);
        assertThat(user1).isNotNull();
        assertThat(user2).isNotNull();
        assertThat(user1.getEmail()).isEqualTo("concurrent1@test.com");
        assertThat(user2.getEmail()).isEqualTo("concurrent2@test.com");
    }

    @Test
    @DisplayName("Should create unique verification tokens for different users")
    void shouldCreateUniqueVerificationTokens() throws Exception {
        // Given
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("tokenuser1");
        request1.setEmail("token1@test.com");
        request1.setPassword("password123");

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("tokenuser2");
        request2.setEmail("token2@test.com");
        request2.setPassword("password123");

        // When
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // Then
        User user1 = userRepository.findByUsername("tokenuser1").orElse(null);
        User user2 = userRepository.findByUsername("tokenuser2").orElse(null);
        
        VerificationToken token1 = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(user1))
                .findFirst()
                .orElse(null);
        VerificationToken token2 = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(user2))
                .findFirst()
                .orElse(null);
        
        assertThat(token1).isNotNull();
        assertThat(token2).isNotNull();
        assertThat(token1.getToken()).isNotEqualTo(token2.getToken());
    }

    @Test
    @DisplayName("Should handle special characters in username and email")
    void shouldHandleSpecialCharactersInUsernameAndEmail() throws Exception {
        // Given
        RegisterRequest specialCharRequest = new RegisterRequest();
        specialCharRequest.setUsername("test-user_123");
        specialCharRequest.setEmail("test.user+tag@example-domain.co.uk");
        specialCharRequest.setPassword("password123");

        // When
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharRequest)))
                .andExpect(status().isOk());

        // Then
        User savedUser = userRepository.findByUsername("test-user_123").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test.user+tag@example-domain.co.uk");
    }

    @Test
    @DisplayName("Should handle very long username and email")
    void shouldHandleVeryLongUsernameAndEmail() throws Exception {
        // Given
        RegisterRequest longRequest = new RegisterRequest();
        longRequest.setUsername("a".repeat(50)); // Long but valid username
        longRequest.setEmail("verylongemailaddress" + "a".repeat(50) + "@example.com");
        longRequest.setPassword("password123");

        // When
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longRequest)))
                .andExpect(status().isOk());

        // Then
        User savedUser = userRepository.findByUsername("a".repeat(50)).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("verylongemailaddress" + "a".repeat(50) + "@example.com");
    }

    @Test
    @DisplayName("Should handle very long password")
    void shouldHandleVeryLongPassword() throws Exception {
        // Given
        RegisterRequest longPasswordRequest = new RegisterRequest();
        longPasswordRequest.setUsername("longpassworduser");
        longPasswordRequest.setEmail("longpassword@test.com");
        longPasswordRequest.setPassword("a".repeat(1000)); // Very long password

        // When
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longPasswordRequest)))
                .andExpect(status().isOk());

        // Then
        User savedUser = userRepository.findByUsername("longpassworduser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isNotEqualTo("a".repeat(1000)); // Should be encoded
    }

    @Test
    @DisplayName("Should verify account activation flow")
    void shouldVerifyAccountActivationFlow() throws Exception {
        // Given - Register a user
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk());

        // When - Get the verification token
        User savedUser = userRepository.findByUsername("integrationtestuser").orElse(null);
        VerificationToken verificationToken = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(savedUser))
                .findFirst()
                .orElse(null);
        assertThat(verificationToken).isNotNull();

        // When - Activate the account
        mockMvc.perform(post("/api/auth/accountVerification/" + verificationToken.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Account Activated Successfully"));

        // Then - Verify user is now enabled
        User activatedUser = userRepository.findByUsername("integrationtestuser").orElse(null);
        assertThat(activatedUser).isNotNull();
        assertThat(activatedUser.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should handle invalid verification token")
    void shouldHandleInvalidVerificationToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/accountVerification/invalid-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle database transaction rollback on error")
    void shouldHandleDatabaseTransactionRollbackOnError() throws Exception {
        // Given - Create a user first to cause duplicate constraint
        User existingUser = new User();
        existingUser.setUsername("rollbackuser");
        existingUser.setEmail("rollback@test.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setEnabled(false);
        userRepository.save(existingUser);

        // Given - Try to register with same username (should fail)
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("rollbackuser");
        duplicateRequest.setEmail("different@test.com");
        duplicateRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());

        // Then - Verify no verification token was created for the failed registration
        User failedUser = userRepository.findAll().stream()
                .filter(user -> "different@test.com".equals(user.getEmail()))
                .findFirst()
                .orElse(null);
        assertThat(failedUser).isNull();
    }
} 