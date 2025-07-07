package com.programming.techie.springredditclone.service.auth;

import com.programming.techie.springredditclone.dto.LoginRequest;
import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.unit.UnitTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 */
class AuthServiceTest extends UnitTestBase {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthServiceTest authServiceTest;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    @Override
    protected void setupTestData() {
        super.setupTestData();
        
        // Setup test data
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void testSignup_Success() {
        // Given
        doNothing().when(authService).signup(any(RegisterRequest.class));

        // When
        authService.signup(registerRequest);

        // Then
        verify(authService, times(1)).signup(registerRequest);
    }

    @Test
    void testLogin_Success() {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(null);

        // When
        authService.login(loginRequest);

        // Then
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void testIsLoggedIn_ReturnsTrue() {
        // Given
        when(authService.isLoggedIn()).thenReturn(true);

        // When
        boolean result = authService.isLoggedIn();

        // Then
        assertTrue(result);
        verify(authService, times(1)).isLoggedIn();
    }

    @Test
    void testIsLoggedIn_ReturnsFalse() {
        // Given
        when(authService.isLoggedIn()).thenReturn(false);

        // When
        boolean result = authService.isLoggedIn();

        // Then
        assertFalse(result);
        verify(authService, times(1)).isLoggedIn();
    }
} 