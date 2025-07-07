package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.model.RefreshToken;

public interface RefreshTokenService {
    
    /**
     * Generate a new refresh token
     * @return Generated refresh token
     */
    RefreshToken generateRefreshToken();
    
    /**
     * Validate a refresh token
     * @param token Token to validate
     */
    void validateRefreshToken(String token);
    
    /**
     * Delete a refresh token
     * @param token Token to delete
     */
    void deleteRefreshToken(String token);
}
