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

import java.time.Instant;

@TestConfiguration
public class TestAuthConfig {

    @Bean
    @Primary
    public AuthService testAuthService() {
        return new AuthService() {
            @Override
            public void signup(RegisterRequest registerRequest) {
                // No-op for tests
            }

            @Override
            public User getCurrentUser() {
                // Create a test user for all tests
                User testUser = new User();
                testUser.setUserId(1L);
                testUser.setUsername("testuser");
                testUser.setEmail("test@example.com");
                testUser.setPassword("password");
                testUser.setCreated(Instant.now());
                testUser.setEnabled(true);
                return testUser;
            }

            @Override
            public void verifyAccount(String token) {
                // No-op for tests
            }

            @Override
            public AuthenticationResponse login(LoginRequest loginRequest) {
                return AuthenticationResponse.builder()
                    .authenticationToken("test-token")
                    .refreshToken("test-refresh-token")
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .username("testuser")
                    .build();
            }

            @Override
            public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
                return AuthenticationResponse.builder()
                    .authenticationToken("new-test-token")
                    .refreshToken("new-test-refresh-token")
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .username("testuser")
                    .build();
            }

            @Override
            public boolean isLoggedIn() {
                return true;
            }
        };
    }
} 