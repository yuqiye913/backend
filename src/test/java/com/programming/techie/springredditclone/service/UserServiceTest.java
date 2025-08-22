package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.GetIntroDto;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.UserIntro;
import com.programming.techie.springredditclone.repository.UserIntroRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.impl.UserServiceImpl;
import com.programming.techie.springredditclone.mapper.UserMapper;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.BlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
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

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthService authService;

    @Mock
    private BlockService blockService;

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
        GetIntroDto expectedDto = new GetIntroDto(1L, "testuser", "This is my bio");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(blockService.isBlockedByUser(1L)).thenReturn(false);
        when(blockService.hasBlockedUser(1L)).thenReturn(false);
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.of(testUserIntro));
        when(userMapper.mapToGetIntroDto(testUser, testUserIntro)).thenReturn(expectedDto);

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
        GetIntroDto expectedDto = new GetIntroDto(1L, "testuser", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(blockService.isBlockedByUser(1L)).thenReturn(false);
        when(blockService.hasBlockedUser(1L)).thenReturn(false);
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userMapper.mapToGetIntroDto(testUser)).thenReturn(expectedDto);

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

    @Test
    @DisplayName("Should search users by username and return matching users")
    void shouldSearchUsersByUsernameAndReturnMatchingUsers() {
        // Given
        User user1 = createUser(1L, "john_doe");
        User user2 = createUser(2L, "jane_smith");
        User user3 = createUser(3L, "bob_wilson");
        
        List<User> allUsers = Arrays.asList(user1, user2, user3);
        when(userRepository.findAll()).thenReturn(allUsers);
        
        GetIntroDto dto1 = new GetIntroDto(1L, "john_doe", "John's bio");
        
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userMapper.mapToGetIntroDto(user1)).thenReturn(dto1);

        // When
        List<GetIntroDto> result = userService.searchUsersByUsername("john");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john_doe");
    }

    @Test
    @DisplayName("Should search users by username case insensitive")
    void shouldSearchUsersByUsernameCaseInsensitive() {
        // Given
        User user1 = createUser(1L, "JohnDoe");
        User user2 = createUser(2L, "JaneSmith");
        
        List<User> allUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(allUsers);
        
        GetIntroDto dto1 = new GetIntroDto(1L, "JohnDoe", "John's bio");
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userMapper.mapToGetIntroDto(user1)).thenReturn(dto1);

        // When
        List<GetIntroDto> result = userService.searchUsersByUsername("johndoe");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("JohnDoe");
    }

    @Test
    @DisplayName("Should return empty list when no users match search term")
    void shouldReturnEmptyListWhenNoUsersMatchSearchTerm() {
        // Given
        User user1 = createUser(1L, "john_doe");
        User user2 = createUser(2L, "jane_smith");
        
        List<User> allUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(allUsers);

        // When
        List<GetIntroDto> result = userService.searchUsersByUsername("xyz");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return users with intro data when available")
    void shouldReturnUsersWithIntroDataWhenAvailable() {
        // Given
        User user1 = createUser(1L, "john_doe");
        UserIntro userIntro1 = createUserIntro(user1, "John's detailed bio");
        
        List<User> allUsers = Arrays.asList(user1);
        when(userRepository.findAll()).thenReturn(allUsers);
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.of(userIntro1));
        
        GetIntroDto dto1 = new GetIntroDto(1L, "john_doe", "John's detailed bio");
        when(userMapper.mapToGetIntroDto(user1, userIntro1)).thenReturn(dto1);

        // When
        List<GetIntroDto> result = userService.searchUsersByUsername("john");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john_doe");
        assertThat(result.get(0).getBio()).isEqualTo("John's detailed bio");
    }

    @Test
    @DisplayName("Should return multiple users when search term matches multiple usernames")
    void shouldReturnMultipleUsersWhenSearchTermMatchesMultipleUsernames() {
        // Given
        User user1 = createUser(1L, "john_doe");
        User user2 = createUser(2L, "john_smith");
        User user3 = createUser(3L, "jane_doe");
        
        List<User> allUsers = Arrays.asList(user1, user2, user3);
        when(userRepository.findAll()).thenReturn(allUsers);
        
        GetIntroDto dto1 = new GetIntroDto(1L, "john_doe", null);
        GetIntroDto dto2 = new GetIntroDto(2L, "john_smith", null);
        
        when(userIntroRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userIntroRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(userMapper.mapToGetIntroDto(user1)).thenReturn(dto1);
        when(userMapper.mapToGetIntroDto(user2)).thenReturn(dto2);

        // When
        List<GetIntroDto> result = userService.searchUsersByUsername("john");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("username").containsExactlyInAnyOrder("john_doe", "john_smith");
    }

    private User createUser(Long userId, String username) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("password");
        user.setCreated(Instant.now());
        user.setEnabled(true);
        return user;
    }

    private UserIntro createUserIntro(User user, String bio) {
        UserIntro userIntro = new UserIntro();
        userIntro.setId(user.getUserId());
        userIntro.setUser(user);
        userIntro.setBio(bio);
        return userIntro;
    }
} 