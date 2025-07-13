package com.programming.techie.springredditclone.repository.matching;

import com.programming.techie.springredditclone.model.Follow;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.FollowRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;
    private Follow follow1;
    private Follow follow2;

    @BeforeEach
    void setUp() {
        // Create test users
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setCreated(Instant.now());
        user1.setEnabled(true);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setCreated(Instant.now());
        user2.setEnabled(true);

        user3 = new User();
        user3.setUsername("user3");
        user3.setEmail("user3@example.com");
        user3.setPassword("password");
        user3.setCreated(Instant.now());
        user3.setEnabled(true);

        // Save users
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        // Create follow relationships
        follow1 = new Follow();
        follow1.setFollower(user1);
        follow1.setFollowing(user2);
        follow1.setActive(true);

        follow2 = new Follow();
        follow2.setFollower(user1);
        follow2.setFollowing(user3);
        follow2.setActive(true);

        // Save follows
        follow1 = followRepository.save(follow1);
        follow2 = followRepository.save(follow2);
    }

    @Test
    @DisplayName("Should save follow relationship")
    void shouldSaveFollowRelationship() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password");
        newUser.setCreated(Instant.now());
        newUser.setEnabled(true);
        newUser = userRepository.save(newUser);

        Follow newFollow = new Follow();
        newFollow.setFollower(user1);
        newFollow.setFollowing(newUser);
        newFollow.setActive(true);

        // When
        Follow savedFollow = followRepository.save(newFollow);

        // Then
        assertThat(savedFollow.getId()).isNotNull();
        assertThat(savedFollow.getFollower()).isEqualTo(user1);
        assertThat(savedFollow.getFollowing()).isEqualTo(newUser);
        assertThat(savedFollow.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should find follow relationship by follower and following")
    void shouldFindFollowByFollowerAndFollowing() {
        // When
        Optional<Follow> foundFollow = followRepository.findByFollowerAndFollowing(user1, user2);

        // Then
        assertThat(foundFollow).isPresent();
        assertThat(foundFollow.get().getFollower()).isEqualTo(user1);
        assertThat(foundFollow.get().getFollowing()).isEqualTo(user2);
    }

    @Test
    @DisplayName("Should return empty when follow relationship does not exist")
    void shouldReturnEmptyWhenFollowDoesNotExist() {
        // When
        Optional<Follow> foundFollow = followRepository.findByFollowerAndFollowing(user2, user1);

        // Then
        assertThat(foundFollow).isEmpty();
    }

    @Test
    @DisplayName("Should check if follow relationship exists")
    void shouldCheckIfFollowExists() {
        // When
        boolean exists = followRepository.existsByFollowerAndFollowing(user1, user2);
        boolean notExists = followRepository.existsByFollowerAndFollowing(user2, user1);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find all follows by follower")
    void shouldFindAllFollowsByFollower() {
        // When
        List<Follow> follows = followRepository.findByFollower(user1);

        // Then
        assertThat(follows).hasSize(2);
        assertThat(follows).allMatch(follow -> follow.getFollower().equals(user1));
    }

    @Test
    @DisplayName("Should find all follows by following")
    void shouldFindAllFollowsByFollowing() {
        // When
        List<Follow> follows = followRepository.findByFollowing(user2);

        // Then
        assertThat(follows).hasSize(1);
        assertThat(follows.get(0).getFollowing()).isEqualTo(user2);
    }

    @Test
    @DisplayName("Should find active follows only")
    void shouldFindActiveFollowsOnly() {
        // Given
        Follow inactiveFollow = new Follow();
        inactiveFollow.setFollower(user2);
        inactiveFollow.setFollowing(user1);
        inactiveFollow.setActive(false);
        followRepository.save(inactiveFollow);

        // When
        List<Follow> activeFollows = followRepository.findActiveFollowingByUser(user1);

        // Then
        assertThat(activeFollows).hasSize(2);
        assertThat(activeFollows).allMatch(Follow::isActive);
    }

    @Test
    @DisplayName("Should delete follow relationship")
    void shouldDeleteFollowRelationship() {
        // Given
        Long followId = follow1.getId();

        // When
        followRepository.delete(follow1);

        // Then
        Optional<Follow> deletedFollow = followRepository.findById(followId);
        assertThat(deletedFollow).isEmpty();
    }

    @Test
    @DisplayName("Should update follow relationship")
    void shouldUpdateFollowRelationship() {
        // Given
        follow1.setActive(false);

        // When
        Follow updatedFollow = followRepository.save(follow1);

        // Then
        assertThat(updatedFollow.isActive()).isFalse();
        
        Optional<Follow> foundFollow = followRepository.findById(follow1.getId());
        assertThat(foundFollow).isPresent();
        assertThat(foundFollow.get().isActive()).isFalse();
    }

    // ========== FOLLOW CONDITION TESTS ==========
    @Test
    @DisplayName("Should return true when user is following another user")
    void shouldReturnTrueWhenUserIsFollowing() {
        // When
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(user1, user2);

        // Then
        assertThat(isFollowing).isTrue();
    }

    @Test
    @DisplayName("Should return false when user is not following another user")
    void shouldReturnFalseWhenUserIsNotFollowing() {
        // When
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(user2, user1);

        // Then
        assertThat(isFollowing).isFalse();
    }

    @Test
    @DisplayName("Should return false when checking self-follow condition")
    void shouldReturnFalseWhenCheckingSelfFollowCondition() {
        // When
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(user1, user1);

        // Then
        assertThat(isFollowing).isFalse();
    }

    @Test
    @DisplayName("Should handle multiple follow conditions correctly")
    void shouldHandleMultipleFollowConditions() {
        // When
        boolean user1FollowingUser2 = followRepository.existsByFollowerAndFollowing(user1, user2);
        boolean user1FollowingUser3 = followRepository.existsByFollowerAndFollowing(user1, user3);
        boolean user2FollowingUser1 = followRepository.existsByFollowerAndFollowing(user2, user1);
        boolean user3FollowingUser1 = followRepository.existsByFollowerAndFollowing(user3, user1);

        // Then
        assertThat(user1FollowingUser2).isTrue();
        assertThat(user1FollowingUser3).isTrue();
        assertThat(user2FollowingUser1).isFalse();
        assertThat(user3FollowingUser1).isFalse();
    }

    @Test
    @DisplayName("Should handle follow condition after follow relationship is deleted")
    void shouldHandleFollowConditionAfterDeletion() {
        // Given
        boolean beforeDeletion = followRepository.existsByFollowerAndFollowing(user1, user2);
        assertThat(beforeDeletion).isTrue();

        // When
        followRepository.delete(follow1);
        boolean afterDeletion = followRepository.existsByFollowerAndFollowing(user1, user2);

        // Then
        assertThat(afterDeletion).isFalse();
    }

    @Test
    @DisplayName("Should handle follow condition with inactive follow relationship")
    void shouldHandleFollowConditionWithInactiveFollow() {
        // Given
        follow1.setActive(false);
        followRepository.save(follow1);

        // When
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(user1, user2);

        // Then
        assertThat(isFollowing).isTrue(); // existsByFollowerAndFollowing doesn't check active status
    }

    @Test
    @DisplayName("Should handle follow condition with new follow relationship")
    void shouldHandleFollowConditionWithNewFollow() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password");
        newUser.setCreated(Instant.now());
        newUser.setEnabled(true);
        newUser = userRepository.save(newUser);

        // When - Before creating follow relationship
        boolean beforeFollow = followRepository.existsByFollowerAndFollowing(user1, newUser);

        // Then
        assertThat(beforeFollow).isFalse();

        // When - After creating follow relationship
        Follow newFollow = new Follow();
        newFollow.setFollower(user1);
        newFollow.setFollowing(newUser);
        newFollow.setActive(true);
        followRepository.save(newFollow);

        boolean afterFollow = followRepository.existsByFollowerAndFollowing(user1, newUser);

        // Then
        assertThat(afterFollow).isTrue();
    }

    @Test
    @DisplayName("Should handle bidirectional follow conditions")
    void shouldHandleBidirectionalFollowConditions() {
        // Given - Create bidirectional follow relationship
        Follow reverseFollow = new Follow();
        reverseFollow.setFollower(user2);
        reverseFollow.setFollowing(user1);
        reverseFollow.setActive(true);
        followRepository.save(reverseFollow);

        // When
        boolean user1FollowingUser2 = followRepository.existsByFollowerAndFollowing(user1, user2);
        boolean user2FollowingUser1 = followRepository.existsByFollowerAndFollowing(user2, user1);

        // Then
        assertThat(user1FollowingUser2).isTrue();
        assertThat(user2FollowingUser1).isTrue();
    }

    @Test
    @DisplayName("Should handle follow condition with null parameters")
    void shouldHandleFollowConditionWithNullParameters() {
        // When & Then
        assertThatThrownBy(() -> followRepository.existsByFollowerAndFollowing(null, user2))
                .isInstanceOf(Exception.class);

        assertThatThrownBy(() -> followRepository.existsByFollowerAndFollowing(user1, null))
                .isInstanceOf(Exception.class);

        assertThatThrownBy(() -> followRepository.existsByFollowerAndFollowing(null, null))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should handle follow condition with non-existent users")
    void shouldHandleFollowConditionWithNonExistentUsers() {
        // Given
        User nonExistentUser = new User();
        nonExistentUser.setUserId(999L);

        // When & Then
        assertThatThrownBy(() -> followRepository.existsByFollowerAndFollowing(nonExistentUser, user2))
                .isInstanceOf(Exception.class);

        assertThatThrownBy(() -> followRepository.existsByFollowerAndFollowing(user1, nonExistentUser))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should handle follow condition performance with multiple relationships")
    void shouldHandleFollowConditionPerformanceWithMultipleRelationships() {
        // Given - Create multiple follow relationships
        User user4 = new User();
        user4.setUsername("user4");
        user4.setEmail("user4@example.com");
        user4.setPassword("password");
        user4.setCreated(Instant.now());
        user4.setEnabled(true);
        user4 = userRepository.save(user4);

        User user5 = new User();
        user5.setUsername("user5");
        user5.setEmail("user5@example.com");
        user5.setPassword("password");
        user5.setCreated(Instant.now());
        user5.setEnabled(true);
        user5 = userRepository.save(user5);

        // Create follow relationships
        Follow follow3 = new Follow();
        follow3.setFollower(user1);
        follow3.setFollowing(user4);
        follow3.setActive(true);
        followRepository.save(follow3);

        Follow follow4 = new Follow();
        follow4.setFollower(user1);
        follow4.setFollowing(user5);
        follow4.setActive(true);
        followRepository.save(follow4);

        // When
        boolean user1FollowingUser2 = followRepository.existsByFollowerAndFollowing(user1, user2);
        boolean user1FollowingUser3 = followRepository.existsByFollowerAndFollowing(user1, user3);
        boolean user1FollowingUser4 = followRepository.existsByFollowerAndFollowing(user1, user4);
        boolean user1FollowingUser5 = followRepository.existsByFollowerAndFollowing(user1, user5);

        // Then
        assertThat(user1FollowingUser2).isTrue();
        assertThat(user1FollowingUser3).isTrue();
        assertThat(user1FollowingUser4).isTrue();
        assertThat(user1FollowingUser5).isTrue();
    }

    @Test
    @DisplayName("Should handle follow condition with edge case user IDs")
    void shouldHandleFollowConditionWithEdgeCaseUserIds() {
        // Given - Create users with edge case IDs
        User userWithZeroId = new User();
        userWithZeroId.setUserId(0L);
        userWithZeroId.setUsername("userZero");
        userWithZeroId.setEmail("userZero@example.com");
        userWithZeroId.setPassword("password");
        userWithZeroId.setCreated(Instant.now());
        userWithZeroId.setEnabled(true);
        userWithZeroId = userRepository.save(userWithZeroId);

        User userWithLargeId = new User();
        userWithLargeId.setUserId(Long.MAX_VALUE);
        userWithLargeId.setUsername("userLarge");
        userWithLargeId.setEmail("userLarge@example.com");
        userWithLargeId.setPassword("password");
        userWithLargeId.setCreated(Instant.now());
        userWithLargeId.setEnabled(true);
        userWithLargeId = userRepository.save(userWithLargeId);

        // Create follow relationship
        Follow edgeCaseFollow = new Follow();
        edgeCaseFollow.setFollower(userWithZeroId);
        edgeCaseFollow.setFollowing(userWithLargeId);
        edgeCaseFollow.setActive(true);
        followRepository.save(edgeCaseFollow);

        // When
        boolean zeroFollowingLarge = followRepository.existsByFollowerAndFollowing(userWithZeroId, userWithLargeId);
        boolean largeFollowingZero = followRepository.existsByFollowerAndFollowing(userWithLargeId, userWithZeroId);

        // Then
        assertThat(zeroFollowingLarge).isTrue();
        assertThat(largeFollowingZero).isFalse();
    }
} 