package com.programming.techie.springredditclone.service.matching;

import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.model.Follow;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.FollowRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.service.AuthService;
import com.programming.techie.springredditclone.service.impl.FollowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private FollowServiceImpl followService;

    @Captor
    private ArgumentCaptor<Follow> followCaptor;

    private User follower;
    private User following;
    private FollowRequestDto followRequest;

    @BeforeEach
    void setUp() {
        // Create test users
        follower = new User();
        follower.setUserId(1L);
        follower.setUsername("follower");
        follower.setEmail("follower@example.com");
        follower.setPassword("password");
        follower.setCreated(Instant.now());
        follower.setEnabled(true);

        following = new User();
        following.setUserId(2L);
        following.setUsername("following");
        following.setEmail("following@example.com");
        following.setPassword("password");
        following.setCreated(Instant.now());
        following.setEnabled(true);

        // Create follow request
        followRequest = new FollowRequestDto();
        followRequest.setFollowingId(2L);
    }

    @Test
    @DisplayName("Should successfully follow a user")
    void shouldFollowUser() {
        // Given
        when(authService.getCurrentUser()).thenReturn(follower);
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        followService.followUser(followRequest);

        // Then
        verify(followRepository).save(followCaptor.capture());
        Follow savedFollow = followCaptor.getValue();
        
        assertThat(savedFollow.getFollower()).isEqualTo(follower);
        assertThat(savedFollow.getFollowing()).isEqualTo(following);
        assertThat(savedFollow.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when user to follow not found")
    void shouldThrowExceptionWhenUserToFollowNotFound() {
        // Given
        when(authService.getCurrentUser()).thenReturn(follower);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> followService.followUser(followRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User to follow not found");

        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    @DisplayName("Should throw exception when already following user")
    void shouldThrowExceptionWhenAlreadyFollowing() {
        // Given
        when(authService.getCurrentUser()).thenReturn(follower);
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> followService.followUser(followRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Already following this user");

        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    @DisplayName("Should not allow self-following")
    void shouldNotAllowSelfFollowing() {
        // Given
        followRequest.setFollowingId(1L);

        when(authService.getCurrentUser()).thenReturn(follower);

        // When & Then
        assertThatThrownBy(() -> followService.followUser(followRequest))
                .isInstanceOf(RuntimeException.class);

        verify(followRepository, never()).save(any(Follow.class));
    }
} 