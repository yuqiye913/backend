package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.AuthenticationResponse;
import com.programming.techie.springredditclone.dto.LoginRequest;
import com.programming.techie.springredditclone.dto.RefreshTokenRequest;
import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.model.User;

public interface AuthService {
    
    /**
     * Register a new user
     * @param registerRequest User registration data
     */
    void signup(RegisterRequest registerRequest);
    
    /**
     * Get the currently authenticated user
     * @return Current user
     */
    User getCurrentUser();
    
    /**
     * Verify user account with token
     * @param token Verification token
     */
    void verifyAccount(String token);
    
    /**
     * Authenticate user login
     * @param loginRequest Login credentials
     * @return Authentication response with tokens
     */
    AuthenticationResponse login(LoginRequest loginRequest);
    
    /**
     * Refresh authentication token
     * @param refreshTokenRequest Refresh token request
     * @return New authentication response
     */
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    
    /**
     * Check if user is currently logged in
     * @return true if logged in, false otherwise
     */
    boolean isLoggedIn();
}