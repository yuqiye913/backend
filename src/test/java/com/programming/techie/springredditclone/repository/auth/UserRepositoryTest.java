package com.programming.techie.springredditclone.repository.auth;

import com.programming.techie.springredditclone.model.User;
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

/**
 * Repository tests for User entity
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setCreated(Instant.now());
        testUser.setEnabled(false);
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword123");
        assertThat(savedUser.isEnabled()).isFalse();
        assertThat(savedUser.getCreated()).isNotNull();
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void shouldReturnEmptyWhenUsernameNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should return empty when ID not found")
    void shouldReturnEmptyWhenIdNotFound() {
        // When
        Optional<User> foundUser = userRepository.findById(999L);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        // Given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setCreated(Instant.now());
        user1.setEnabled(true);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setCreated(Instant.now());
        user2.setEnabled(false);

        userRepository.save(user1);
        userRepository.save(user2);

        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting("username").contains("user1", "user2");
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        savedUser.setEnabled(true);
        savedUser.setEmail("updated@example.com");
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertThat(updatedUser.isEnabled()).isTrue();
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");

        // Verify in database
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().isEnabled()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        userRepository.delete(savedUser);

        // Then
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should handle case-sensitive username search")
    void shouldHandleCaseSensitiveUsernameSearch() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<User> foundUser = userRepository.findByUsername("TESTUSER");

        // Then
        assertThat(foundUser).isEmpty(); // Should be case-sensitive
    }

    @Test
    @DisplayName("Should handle null username search")
    void shouldHandleNullUsernameSearch() {
        // When
        Optional<User> foundUser = userRepository.findByUsername(null);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty username search")
    void shouldHandleEmptyUsernameSearch() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should persist user with all fields")
    void shouldPersistUserWithAllFields() {
        // Given
        User user = new User();
        user.setUsername("completeuser");
        user.setEmail("complete@example.com");
        user.setPassword("completePassword123");
        user.setCreated(Instant.now());
        user.setEnabled(true);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("completeuser");
        assertThat(savedUser.getEmail()).isEqualTo("complete@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("completePassword123");
        assertThat(savedUser.isEnabled()).isTrue();
        assertThat(savedUser.getCreated()).isNotNull();
    }
} 