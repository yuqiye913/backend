package com.programming.techie.springredditclone.config;

import com.programming.techie.springredditclone.dto.AuthenticationResponse;
import com.programming.techie.springredditclone.dto.LoginRequest;
import com.programming.techie.springredditclone.dto.RefreshTokenRequest;
import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.service.AuthService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@TestConfiguration
public class TestAuthConfig {

    // Store test users for verification
    private final Map<String, User> testUsers = new HashMap<>();
    private User currentTestUser;

    @Bean
    @Primary
    public AuthService testAuthService() {
        return new AuthService() {
            @Override
            public void signup(RegisterRequest registerRequest) {
                // Create a real user object for testing
                User user = new User();
                user.setUsername(registerRequest.getUsername());
                user.setEmail(registerRequest.getEmail());
                user.setPassword("encoded_" + registerRequest.getPassword()); // Simulate encoding
                user.setCreated(Instant.now());
                user.setEnabled(false); // Start as disabled like real flow
                
                testUsers.put(registerRequest.getUsername(), user);
                currentTestUser = user;
            }

            @Override
            public User getCurrentUser() {
                // Try to get from security context first (more realistic)
                try {
                    if (SecurityContextHolder.getContext().getAuthentication() != null &&
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Jwt) {
                        
                        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        String username = jwt.getSubject();
                        return testUsers.getOrDefault(username, createDefaultTestUser());
                    }
                } catch (Exception e) {
                    // Fall back to test user if security context fails
                }
                
                return currentTestUser != null ? currentTestUser : createDefaultTestUser();
            }

            @Override
            public void verifyAccount(String token) {
                // Simulate account verification
                if (currentTestUser != null) {
                    currentTestUser.setEnabled(true);
                }
            }

            @Override
            public AuthenticationResponse login(LoginRequest loginRequest) {
                // Simulate login and return test tokens
                User user = testUsers.get(loginRequest.getUsername());
                if (user != null && user.isEnabled()) {
                    currentTestUser = user;
                    return AuthenticationResponse.builder()
                        .authenticationToken("test-jwt-token-" + user.getUsername())
                        .refreshToken("test-refresh-token-" + user.getUsername())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .username(user.getUsername())
                        .build();
                }
                throw new RuntimeException("Invalid credentials");
            }

            @Override
            public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
                return AuthenticationResponse.builder()
                    .authenticationToken("new-test-jwt-token")
                    .refreshToken("new-test-refresh-token")
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .username(currentTestUser != null ? currentTestUser.getUsername() : "testuser")
                    .build();
            }

            @Override
            public boolean isLoggedIn() {
                return currentTestUser != null && currentTestUser.isEnabled();
            }
        };
    }

    private User createDefaultTestUser() {
        User testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setCreated(Instant.now());
        testUser.setEnabled(true);
        return testUser;
    }

    /**
     * Helper method to get test users for verification
     */
    public Map<String, User> getTestUsers() {
        return new HashMap<>(testUsers);
    }

    /**
     * Helper method to clear test users
     */
    public void clearTestUsers() {
        testUsers.clear();
        currentTestUser = null;
    }

    /**
     * Helper method to set current test user
     */
    public void setCurrentTestUser(User user) {
        this.currentTestUser = user;
        if (user != null) {
            testUsers.put(user.getUsername(), user);
        }
    }
} 