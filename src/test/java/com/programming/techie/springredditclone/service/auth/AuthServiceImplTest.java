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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
} 