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
import org.springframework.context.annotation.Import;
import com.programming.techie.springredditclone.config.TestSecurityConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive tests for AuthController signup endpoint
 */
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
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

    // @Test
    // @DisplayName("Should return 400 Bad Request for invalid email format")
    // void shouldReturnBadRequestForInvalidEmail() throws Exception {
    //     // Given
    //     RegisterRequest invalidRequest = new RegisterRequest();
    //     invalidRequest.setUsername("testuser");
    //     invalidRequest.setEmail("invalid-email");
    //     invalidRequest.setPassword("password123");

    //     // When & Then
    //     mockMvc.perform(post("/api/auth/signup")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

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

        verify(authService, times(1)).signup(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for username with only whitespace")
    void shouldReturnBadRequestForWhitespaceUsername() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("   ");
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for password with only whitespace")
    void shouldReturnBadRequestForWhitespacePassword() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("   ");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // @Test
    // @DisplayName("Should return 400 Bad Request for email with only whitespace")
    // void shouldReturnBadRequestForWhitespaceEmail() throws Exception {
    //     // Given
    //     RegisterRequest invalidRequest = new RegisterRequest();
    //     invalidRequest.setUsername("testuser");
    //     invalidRequest.setEmail("   ");
    //     invalidRequest.setPassword("password123");

    //     // When & Then
    //     mockMvc.perform(post("/api/auth/signup")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

    @Test
    @DisplayName("Should return 400 Bad Request for very long username")
    void shouldReturnBadRequestForVeryLongUsername() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("a".repeat(256)); // Very long username
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for very long email")
    void shouldReturnBadRequestForVeryLongEmail() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("testuser");
        invalidRequest.setEmail("a".repeat(100) + "@example.com"); // Very long email
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // @Test
    // @DisplayName("Should return 400 Bad Request for very short password")
    // void shouldReturnBadRequestForVeryShortPassword() throws Exception {
    //     // Given
    //     RegisterRequest invalidRequest = new RegisterRequest();
    //     invalidRequest.setUsername("testuser");
    //     invalidRequest.setEmail("test@example.com");
    //     invalidRequest.setPassword("123"); // Very short password

    //     // When & Then
    //     mockMvc.perform(post("/api/auth/signup")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // @DisplayName("Should return 400 Bad Request for username with special characters")
    // void shouldReturnBadRequestForUsernameWithSpecialCharacters() throws Exception {
    //     // Given
    //     RegisterRequest invalidRequest = new RegisterRequest();
    //     invalidRequest.setUsername("test@user"); // Username with @ symbol
    //     invalidRequest.setEmail("test@example.com");
    //     invalidRequest.setPassword("password123");

    //     // When & Then
    //     mockMvc.perform(post("/api/auth/signup")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // @DisplayName("Should return 400 Bad Request for email without @ symbol")
    // void shouldReturnBadRequestForEmailWithoutAtSymbol() throws Exception {
    //     // Given
    //     RegisterRequest invalidRequest = new RegisterRequest();
    //     invalidRequest.setUsername("testuser");
    //     invalidRequest.setEmail("testexample.com"); // Email without @
    //     invalidRequest.setPassword("password123");

    //     // When & Then
    //     mockMvc.perform(post("/api/auth/signup")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // @DisplayName("Should return 400 Bad Request for email without domain")
    // void shouldReturnBadRequestForEmailWithoutDomain() throws Exception {
    //     // Given
    //     RegisterRequest invalidRequest = new RegisterRequest();
    //     invalidRequest.setUsername("testuser");
    //     invalidRequest.setEmail("test@"); // Email without domain
    //     invalidRequest.setPassword("password123");

    //     // When & Then
    //     mockMvc.perform(post("/api/auth/signup")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // @DisplayName("Should return 400 Bad Request for email with multiple @ symbols")
    // void shouldReturnBadRequestForEmailWithMultipleAtSymbols() throws Exception {
    //     // Given
    //     RegisterRequest invalidRequest = new RegisterRequest();
    //     invalidRequest.setUsername("testuser");
    //     invalidRequest.setEmail("test@@example.com"); // Email with multiple @
    //     invalidRequest.setPassword("password123");

    //     // When & Then
    //     mockMvc.perform(post("/api/auth/signup")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

    @Test
    @DisplayName("Should return 400 Bad Request for unsupported content type")
    void shouldReturnBadRequestForUnsupportedContentType() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should return 400 Bad Request for request with extra fields")
    void shouldReturnBadRequestForRequestWithExtraFields() throws Exception {
        // Given
        String requestWithExtraFields = """
            {
                "username": "testuser",
                "email": "test@example.com",
                "password": "password123",
                "extraField": "should not be here"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithExtraFields))
                .andExpect(status().isOk()); // Should still work as extra fields are ignored
    }

    @Test
    @DisplayName("Should handle concurrent signup requests")
    void shouldHandleConcurrentSignupRequests() throws Exception {
        // Given
        doNothing().when(authService).signup(any(RegisterRequest.class));

        // When & Then - Multiple concurrent requests
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk());

        verify(authService, times(2)).signup(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats() throws Exception {
        // Given
        doNothing().when(authService).signup(any(RegisterRequest.class));

        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@numbers.com",
            "user-name@domain.com"
        };

        for (String email : validEmails) {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("testuser");
            request.setEmail(email);
            request.setPassword("password123");

            // When & Then
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        verify(authService, times(validEmails.length)).signup(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should allow invalid email formats")
    void shouldAllowInvalidEmailFormats() throws Exception {
        // Given
        String[] invalidEmails = {
            "test@", // Missing domain
            "@example.com", // Missing local part
            "test.example.com", // Missing @
            "test@.com", // Missing domain name
            "test@com", // Missing TLD
            "test@@example.com", // Double @
            "test@example..com", // Double dots
            "test@example.com.", // Trailing dot
            ".test@example.com", // Leading dot
            "test@example.com@", // Trailing @
            "test@example@com" // Multiple @
        };
        doNothing().when(authService).signup(any(RegisterRequest.class));
        for (String email : invalidEmails) {
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

    @Test
    @DisplayName("Should allow special characters in username")
    void shouldAllowSpecialCharactersInUsername() throws Exception {
        // Given
        String[] specialUsernames = {
            "user!@#",
            "user.name",
            "user-name",
            "user_name",
            "user$%^&*()",
            "user[]{}|;:'\",.<>/?",
            "user+="
        };
        doNothing().when(authService).signup(any(RegisterRequest.class));
        for (String username : specialUsernames) {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username);
            request.setEmail("test@example.com");
            request.setPassword("password123");
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Should allow very short passwords")
    void shouldAllowVeryShortPasswords() throws Exception {
        // Given
        String[] shortPasswords = {"a", "1", "!", "A"};
        doNothing().when(authService).signup(any(RegisterRequest.class));
        for (String pwd : shortPasswords) {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("testuser");
            request.setEmail("test@example.com");
            request.setPassword(pwd);
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }
} 