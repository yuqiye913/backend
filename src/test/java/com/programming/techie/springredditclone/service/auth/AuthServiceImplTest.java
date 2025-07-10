package com.programming.techie.springredditclone.service.auth;

import com.programming.techie.springredditclone.dto.RegisterRequest;
import com.programming.techie.springredditclone.model.NotificationEmail;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.model.VerificationToken;
import com.programming.techie.springredditclone.repository.UserRepository;
import com.programming.techie.springredditclone.repository.VerificationTokenRepository;
import com.programming.techie.springredditclone.service.impl.AuthServiceImpl;
import com.programming.techie.springredditclone.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

/**
 * Unit tests for AuthServiceImpl signup functionality
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    
    @Mock
    private MailService mailService;

    @Captor
    private ArgumentCaptor<User> userCaptor;
    
    @Captor
    private ArgumentCaptor<VerificationToken> tokenCaptor;
    
    @Captor
    private ArgumentCaptor<NotificationEmail> emailCaptor;

    private AuthServiceImpl authService;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
            passwordEncoder, userRepository, verificationTokenRepository, 
            mailService, null, null, null
        );
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void shouldRegisterNewUser() {
        // Given
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(registerRequest);

        // Then
        verify(userRepository).save(userCaptor.capture());
        verify(verificationTokenRepository).save(tokenCaptor.capture());
        verify(mailService).sendMail(emailCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.isEnabled()).isFalse();
        assertThat(savedUser.getCreated()).isNotNull();

        VerificationToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUser()).isEqualTo(savedUser);
        assertThat(savedToken.getToken()).isNotNull();

        NotificationEmail sentEmail = emailCaptor.getValue();
        assertThat(sentEmail.getSubject()).isEqualTo("Please Activate your Account");
        assertThat(sentEmail.getRecipient()).isEqualTo("test@example.com");
        assertThat(sentEmail.getBody()).contains("Thank you for signing up to Spring Reddit");
        assertThat(sentEmail.getBody()).contains(savedToken.getToken());
    }

    @Test
    @DisplayName("Should encode password during registration")
    void shouldEncodePassword() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(registerRequest);

        // Then
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Should create user with disabled status initially")
    void shouldCreateUserWithDisabledStatus() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(registerRequest);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should generate verification token for new user")
    void shouldGenerateVerificationToken() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(registerRequest);

        // Then
        verify(verificationTokenRepository).save(tokenCaptor.capture());
        VerificationToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getToken()).isNotNull();
        assertThat(savedToken.getToken()).isNotEmpty();
        assertThat(savedToken.getUser()).isNotNull();
    }

    @Test
    @DisplayName("Should send verification email to user")
    void shouldSendVerificationEmail() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(registerRequest);

        // Then
        verify(mailService).sendMail(emailCaptor.capture());
        NotificationEmail sentEmail = emailCaptor.getValue();
        assertThat(sentEmail.getSubject()).isEqualTo("Please Activate your Account");
        assertThat(sentEmail.getRecipient()).isEqualTo("test@example.com");
        assertThat(sentEmail.getBody()).contains("Thank you for signing up");
        assertThat(sentEmail.getBody()).contains("http://localhost:8080/api/auth/accountVerification/");
    }

    @Test
    @DisplayName("Should handle null register request")
    void shouldHandleNullRegisterRequest() {
        // When & Then
        assertThatThrownBy(() -> authService.signup(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RegisterRequest cannot be null");
    }

    @Test
    @DisplayName("Should handle empty username in register request")
    void shouldHandleEmptyUsername() {
        // Given
        RegisterRequest requestWithEmptyUsername = new RegisterRequest();
        requestWithEmptyUsername.setUsername("");
        requestWithEmptyUsername.setEmail("test@example.com");
        requestWithEmptyUsername.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> authService.signup(requestWithEmptyUsername))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be empty");
    }

    @Test
    @DisplayName("Should handle null username in register request")
    void shouldHandleNullUsername() {
        // Given
        RegisterRequest requestWithNullUsername = new RegisterRequest();
        requestWithNullUsername.setUsername(null);
        requestWithNullUsername.setEmail("test@example.com");
        requestWithNullUsername.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> authService.signup(requestWithNullUsername))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null");
    }

    @Test
    @DisplayName("Should handle empty email in register request")
    void shouldHandleEmptyEmail() {
        // Given
        RegisterRequest requestWithEmptyEmail = new RegisterRequest();
        requestWithEmptyEmail.setUsername("testuser");
        requestWithEmptyEmail.setEmail("");
        requestWithEmptyEmail.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> authService.signup(requestWithEmptyEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be empty");
    }

    @Test
    @DisplayName("Should handle null email in register request")
    void shouldHandleNullEmail() {
        // Given
        RegisterRequest requestWithNullEmail = new RegisterRequest();
        requestWithNullEmail.setUsername("testuser");
        requestWithNullEmail.setEmail(null);
        requestWithNullEmail.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> authService.signup(requestWithNullEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    @DisplayName("Should handle empty password in register request")
    void shouldHandleEmptyPassword() {
        // Given
        RegisterRequest requestWithEmptyPassword = new RegisterRequest();
        requestWithEmptyPassword.setUsername("testuser");
        requestWithEmptyPassword.setEmail("test@example.com");
        requestWithEmptyPassword.setPassword("");

        // When & Then
        assertThatThrownBy(() -> authService.signup(requestWithEmptyPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password cannot be empty");
    }

    @Test
    @DisplayName("Should handle null password in register request")
    void shouldHandleNullPassword() {
        // Given
        RegisterRequest requestWithNullPassword = new RegisterRequest();
        requestWithNullPassword.setUsername("testuser");
        requestWithNullPassword.setEmail("test@example.com");
        requestWithNullPassword.setPassword(null);

        // When & Then
        assertThatThrownBy(() -> authService.signup(requestWithNullPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password cannot be null");
    }

    @Test
    @DisplayName("Should handle user repository save failure")
    void shouldHandleUserRepositorySaveFailure() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> authService.signup(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");

        verify(userRepository).save(any(User.class));
        verify(verificationTokenRepository, never()).save(any(VerificationToken.class));
        verify(mailService, never()).sendMail(any(NotificationEmail.class));
    }

    @Test
    @DisplayName("Should handle verification token repository save failure")
    void shouldHandleVerificationTokenRepositorySaveFailure() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenThrow(new RuntimeException("Token save error"));

        // When & Then
        assertThatThrownBy(() -> authService.signup(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token save error");

        verify(userRepository).save(any(User.class));
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(mailService, never()).sendMail(any(NotificationEmail.class));
    }

    @Test
    @DisplayName("Should handle mail service failure")
    void shouldHandleMailServiceFailure() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("Mail service error")).when(mailService).sendMail(any(NotificationEmail.class));

        // When & Then
        assertThatThrownBy(() -> authService.signup(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mail service error");

        verify(userRepository).save(any(User.class));
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(mailService).sendMail(any(NotificationEmail.class));
    }

    @Test
    @DisplayName("Should handle password encoder failure")
    void shouldHandlePasswordEncoderFailure() {
        // Given
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Password encoding error"));

        // When & Then
        assertThatThrownBy(() -> authService.signup(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Password encoding error");

        verify(passwordEncoder).encode("password123");
        verify(userRepository, never()).save(any(User.class));
        verify(verificationTokenRepository, never()).save(any(VerificationToken.class));
        verify(mailService, never()).sendMail(any(NotificationEmail.class));
    }

    @Test
    @DisplayName("Should create user with correct timestamp")
    void shouldCreateUserWithCorrectTimestamp() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(registerRequest);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getCreated()).isNotNull();
        assertThat(savedUser.getCreated()).isBeforeOrEqualTo(java.time.Instant.now());
    }

    @Test
    @DisplayName("Should generate unique verification tokens for different users")
    void shouldGenerateUniqueVerificationTokens() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        RegisterRequest secondRequest = new RegisterRequest();
        secondRequest.setUsername("testuser2");
        secondRequest.setEmail("test2@example.com");
        secondRequest.setPassword("password123");

        // When
        authService.signup(registerRequest);
        authService.signup(secondRequest);

        // Then
        verify(verificationTokenRepository, times(2)).save(tokenCaptor.capture());
        List<VerificationToken> savedTokens = tokenCaptor.getAllValues();
        assertThat(savedTokens).hasSize(2);
        assertThat(savedTokens.get(0).getToken()).isNotEqualTo(savedTokens.get(1).getToken());
    }

    @Test
    @DisplayName("Should handle special characters in username and email")
    void shouldHandleSpecialCharactersInUsernameAndEmail() {
        // Given
        RegisterRequest specialCharRequest = new RegisterRequest();
        specialCharRequest.setUsername("test-user_123");
        specialCharRequest.setEmail("test.user+tag@example-domain.co.uk");
        specialCharRequest.setPassword("password123");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(specialCharRequest);

        // Then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("test-user_123");
        assertThat(savedUser.getEmail()).isEqualTo("test.user+tag@example-domain.co.uk");
    }

    @Test
    @DisplayName("Should handle very long password")
    void shouldHandleVeryLongPassword() {
        // Given
        RegisterRequest longPasswordRequest = new RegisterRequest();
        longPasswordRequest.setUsername("testuser");
        longPasswordRequest.setEmail("test@example.com");
        longPasswordRequest.setPassword("a".repeat(1000)); // Very long password

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(longPasswordRequest);

        // Then
        verify(passwordEncoder).encode("a".repeat(1000));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should verify that user is saved before token generation")
    void shouldSaveUserBeforeTokenGeneration() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // When
        authService.signup(registerRequest);

        // Then
        InOrder inOrder = inOrder(userRepository, verificationTokenRepository, mailService);
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(verificationTokenRepository).save(any(VerificationToken.class));
        inOrder.verify(mailService).sendMail(any(NotificationEmail.class));
    }
} 