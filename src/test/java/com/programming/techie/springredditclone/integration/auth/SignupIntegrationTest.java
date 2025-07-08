package com.programming.techie.springredditclone.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.BaseTest;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for complete signup flow
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SignupIntegrationTest extends BaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("integrationtestuser");
        registerRequest.setEmail("integration@test.com");
        registerRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should complete full signup flow with account verification")
    void shouldCompleteFullSignupFlow() throws Exception {
        // Given - Clean state
        userRepository.deleteAll();
        verificationTokenRepository.deleteAll();

        // When & Then - Step 1: Register user
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registration Successful"));

        // Verify user was created in database
        User savedUser = userRepository.findByUsername("integrationtestuser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("integration@test.com");
        assertThat(savedUser.isEnabled()).isFalse(); // User should be disabled initially

        // Verify verification token was created
        // We need to find the token by searching through all tokens since findByUser doesn't exist
        VerificationToken verificationToken = verificationTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(savedUser))
                .findFirst()
                .orElse(null);
        assertThat(verificationToken).isNotNull();
        assertThat(verificationToken.getToken()).isNotNull();
        assertThat(verificationToken.getToken()).isNotEmpty();

        // Step 2: Verify account with token
        mockMvc.perform(get("/api/auth/accountVerification/" + verificationToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Account Activated Successfully"));

        // Verify user is now enabled
        User verifiedUser = userRepository.findByUsername("integrationtestuser").orElse(null);
        assertThat(verifiedUser).isNotNull();
        assertThat(verifiedUser.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should handle duplicate username registration")
    void shouldHandleDuplicateUsername() throws Exception {
        // Given - Create first user
        userRepository.deleteAll();
        
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // When & Then - Try to register with same username
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("integrationtestuser");
        duplicateRequest.setEmail("different@test.com");
        duplicateRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isInternalServerError()); // Should handle duplicate gracefully
    }

    @Test
    @DisplayName("Should handle duplicate email registration")
    void shouldHandleDuplicateEmail() throws Exception {
        // Given - Create first user
        userRepository.deleteAll();
        
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // When & Then - Try to register with same email
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("differentuser");
        duplicateRequest.setEmail("integration@test.com");
        duplicateRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isInternalServerError()); // Should handle duplicate gracefully
    }

    @Test
    @DisplayName("Should handle invalid verification token")
    void shouldHandleInvalidVerificationToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/accountVerification/invalid-token"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle missing verification token")
    void shouldHandleMissingVerificationToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/accountVerification/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should persist user data correctly")
    void shouldPersistUserDataCorrectly() throws Exception {
        // Given
        userRepository.deleteAll();

        // When
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Then
        User savedUser = userRepository.findByUsername("integrationtestuser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("integrationtestuser");
        assertThat(savedUser.getEmail()).isEqualTo("integration@test.com");
        assertThat(savedUser.getPassword()).isNotEqualTo("password123"); // Should be encoded
        assertThat(savedUser.getCreated()).isNotNull();
        assertThat(savedUser.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should create verification token with correct user association")
    void shouldCreateVerificationTokenWithCorrectUserAssociation() throws Exception {
        // Given
        userRepository.deleteAll();
        verificationTokenRepository.deleteAll();

        // When
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Then
        User savedUser = userRepository.findByUsername("integrationtestuser").orElse(null);
        assertThat(savedUser).isNotNull();

        VerificationToken token = verificationTokenRepository.findAll().stream()
                .filter(t -> t.getUser().equals(savedUser))
                .findFirst()
                .orElse(null);
        assertThat(token).isNotNull();
        assertThat(token.getUser()).isEqualTo(savedUser);
        assertThat(token.getToken()).isNotNull();
        assertThat(token.getToken()).hasSize(36); // UUID length
    }
} 