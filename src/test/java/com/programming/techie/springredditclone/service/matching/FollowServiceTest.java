package com.programming.techie.springredditclone.service.matching;

import com.programming.techie.springredditclone.dto.FollowRequestDto;
import com.programming.techie.springredditclone.dto.GetFollowersDto;
import com.programming.techie.springredditclone.dto.FollowerCountDto;
import com.programming.techie.springredditclone.dto.FollowingCountDto;
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
import java.util.Arrays;
import java.util.List;
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
    private Follow follow1;
    private Follow follow2;

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

        // Create follow relationships
        follow1 = new Follow();
        follow1.setId(1L);
        follow1.setFollower(follower);
        follow1.setFollowing(following);
        follow1.setFollowedAt(Instant.now());
        follow1.setActive(true);
        follow1.setMuted(false);
        follow1.setCloseFriend(false);

        follow2 = new Follow();
        follow2.setId(2L);
        follow2.setFollower(following);
        follow2.setFollowing(follower);
        follow2.setFollowedAt(Instant.now());
        follow2.setActive(true);
        follow2.setMuted(false);
        follow2.setCloseFriend(false);
    }

    // ========== FOLLOW USER TESTS ==========
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
                .isInstanceOf(RuntimeException.class);

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
                .isInstanceOf(RuntimeException.class);

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

    // ========== UNFOLLOW USER TESTS ==========
    @Test
    @DisplayName("Should successfully unfollow a user")
    void shouldUnfollowUser() {
        // Given
        when(authService.getCurrentUser()).thenReturn(follower);
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(follow1));

        // When
        followService.unfollowUser(2L);

        // Then
        verify(followRepository).delete(follow1);
    }

    @Test
    @DisplayName("Should throw exception when user to unfollow not found")
    void shouldThrowExceptionWhenUserToUnfollowNotFound() {
        // Given
        when(authService.getCurrentUser()).thenReturn(follower);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> followService.unfollowUser(999L))
                .isInstanceOf(RuntimeException.class);

        verify(followRepository, never()).delete(any(Follow.class));
    }

    @Test
    @DisplayName("Should throw exception when not following user")
    void shouldThrowExceptionWhenNotFollowingUser() {
        // Given
        when(authService.getCurrentUser()).thenReturn(follower);
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> followService.unfollowUser(2L))
                .isInstanceOf(RuntimeException.class);

        verify(followRepository, never()).delete(any(Follow.class));
    }

    @Test
    @DisplayName("Should not allow self-unfollowing")
    void shouldNotAllowSelfUnfollowing() {
        // Given
        when(authService.getCurrentUser()).thenReturn(follower);

        // When & Then
        assertThatThrownBy(() -> followService.unfollowUser(1L))
                .isInstanceOf(RuntimeException.class);

        verify(followRepository, never()).delete(any(Follow.class));
    }

    // ========== GET FOLLOWERS TESTS ==========
    @Test
    @DisplayName("Should get followers by user ID")
    void shouldGetFollowersByUserId() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findActiveFollowersByUser(following)).thenReturn(Arrays.asList(follow1));

        // When
        List<GetFollowersDto> result = followService.getFollowersByUserId(2L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        assertThat(result.get(0).getUsername()).isEqualTo("follower");
        assertThat(result.get(0).getEmail()).isEqualTo("follower@example.com");
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when user not found for getting followers")
    void shouldThrowExceptionWhenUserNotFoundForGettingFollowers() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> followService.getFollowersByUserId(999L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should return empty list when user has no followers")
    void shouldReturnEmptyListWhenUserHasNoFollowers() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findActiveFollowersByUser(following)).thenReturn(Arrays.asList());

        // When
        List<GetFollowersDto> result = followService.getFollowersByUserId(2L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return multiple followers")
    void shouldReturnMultipleFollowers() {
        // Given
        User user3 = new User();
        user3.setUserId(3L);
        user3.setUsername("user3");
        user3.setEmail("user3@example.com");

        Follow follow3 = new Follow();
        follow3.setId(3L);
        follow3.setFollower(user3);
        follow3.setFollowing(following);
        follow3.setFollowedAt(Instant.now());
        follow3.setActive(true);

        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.findActiveFollowersByUser(following)).thenReturn(Arrays.asList(follow1, follow3));

        // When
        List<GetFollowersDto> result = followService.getFollowersByUserId(2L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        assertThat(result.get(1).getUserId()).isEqualTo(3L);
    }

    // ========== GET FOLLOWER COUNT TESTS ==========
    @Test
    @DisplayName("Should get follower count by user ID")
    void shouldGetFollowerCountByUserId() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.countFollowersByUser(following)).thenReturn(5L);

        // When
        FollowerCountDto result = followService.getFollowerCountByUserId(2L);

        // Then
        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getUsername()).isEqualTo("following");
        assertThat(result.getFollowerCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should throw exception when user not found for getting follower count")
    void shouldThrowExceptionWhenUserNotFoundForGettingFollowerCount() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> followService.getFollowerCountByUserId(999L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should return zero count when user has no followers")
    void shouldReturnZeroCountWhenUserHasNoFollowers() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.countFollowersByUser(following)).thenReturn(0L);

        // When
        FollowerCountDto result = followService.getFollowerCountByUserId(2L);

        // Then
        assertThat(result.getFollowerCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should return large follower count")
    void shouldReturnLargeFollowerCount() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.countFollowersByUser(following)).thenReturn(1000L);

        // When
        FollowerCountDto result = followService.getFollowerCountByUserId(2L);

        // Then
        assertThat(result.getFollowerCount()).isEqualTo(1000L);
    }

    // ========== GET FOLLOWING COUNT TESTS ==========
    @Test
    @DisplayName("Should get following count by user ID")
    void shouldGetFollowingCountByUserId() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.countFollowingByUser(following)).thenReturn(5L);

        // When
        FollowingCountDto result = followService.getFollowingCountByUserId(2L);

        // Then
        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getUsername()).isEqualTo("following");
        assertThat(result.getFollowingCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should throw exception when user not found for getting following count")
    void shouldThrowExceptionWhenUserNotFoundForGettingFollowingCount() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> followService.getFollowingCountByUserId(999L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should return zero count when user is not following anyone")
    void shouldReturnZeroCountWhenUserIsNotFollowingAnyone() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.countFollowingByUser(following)).thenReturn(0L);

        // When
        FollowingCountDto result = followService.getFollowingCountByUserId(2L);

        // Then
        assertThat(result.getFollowingCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should return large following count")
    void shouldReturnLargeFollowingCount() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));
        when(followRepository.countFollowingByUser(following)).thenReturn(1000L);

        // When
        FollowingCountDto result = followService.getFollowingCountByUserId(2L);

        // Then
        assertThat(result.getFollowingCount()).isEqualTo(1000L);
    }
} 