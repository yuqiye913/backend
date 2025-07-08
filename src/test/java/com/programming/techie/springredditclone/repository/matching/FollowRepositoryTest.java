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
} 