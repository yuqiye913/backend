package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.GetIntroDto;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.UserIntro;
import com.programming.techie.springredditclone.repository.UserIntroRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserIntroRepository userIntroRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserIntro testUserIntro;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setCreated(Instant.now());
        testUser.setEnabled(true);

        // Create test user intro
        testUserIntro = new UserIntro();
        testUserIntro.setId(1L);
        testUserIntro.setUser(testUser);
        testUserIntro.setBio("This is my bio");
    }

    @Test
    @DisplayName("Should get user intro when intro exists")
    void shouldGetUserIntroWhenIntroExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.of(testUserIntro));

        // When
        GetIntroDto result = userService.getUserIntro(1L);

        // Then
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getBio()).isEqualTo("This is my bio");
    }

    @Test
    @DisplayName("Should return basic user info when no intro exists")
    void shouldReturnBasicUserInfoWhenNoIntroExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // When
        GetIntroDto result = userService.getUserIntro(1L);

        // Then
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getBio()).isNull();
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserIntro(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
} 