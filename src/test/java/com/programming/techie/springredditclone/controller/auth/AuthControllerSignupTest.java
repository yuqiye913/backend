package com.programming.techie.springredditclone.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.techie.springredditclone.controller.AuthController;
import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive tests for AuthController signup endpoint
 */
@WebMvcTest(AuthController.class)
class AuthControllerSignupTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("testuser");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should return 200 OK for valid signup request")
    void shouldReturnOkForValidSignup() throws Exception {
        // Given
        doNothing().when(authService).signup(any(RegisterRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registration Successful"));

        verify(authService, times(1)).signup(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for missing username")
    void shouldReturnBadRequestForMissingUsername() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("password123");
        // Username is missing

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for missing email")
    void shouldReturnBadRequestForMissingEmail() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setPassword("password123");
        // Email is missing

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for missing password")
    void shouldReturnBadRequestForMissingPassword() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setEmail("test@example.com");
        // Password is missing

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid email format")
    void shouldReturnBadRequestForInvalidEmail() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for empty username")
    void shouldReturnBadRequestForEmptyUsername() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("");
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for empty password")
    void shouldReturnBadRequestForEmptyPassword() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for empty email")
    void shouldReturnBadRequestForEmptyEmail() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setEmail("");
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for null request body")
    void shouldReturnBadRequestForNullBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for malformed JSON")
    void shouldReturnBadRequestForMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void shouldHandleServiceException() throws Exception {
        // Given
        doThrow(new RuntimeException("Service error")).when(authService).signup(any(RegisterRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats() throws Exception {
        // Given
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@example.com"
        };

        doNothing().when(authService).signup(any(RegisterRequest.class));

        // When & Then
        for (String email : validEmails) {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("testuser");
            request.setEmail(email);
            request.setPassword("password123");

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }
} 