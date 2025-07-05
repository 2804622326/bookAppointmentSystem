package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.event.PasswordResetEvent;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VerificationTokenRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserByPasswordResetToken_found() {
        User user = new User();
        VerificationToken token = new VerificationToken();
        token.setUser(user);

        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));

        Optional<User> result = passwordResetService.findUserByPasswordResetToken("abc123");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testFindUserByPasswordResetToken_notFound() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        Optional<User> result = passwordResetService.findUserByPasswordResetToken("invalid");

        assertTrue(result.isEmpty());
    }

    @Test
    void testRequestPasswordReset_success() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset("test@example.com");

        verify(eventPublisher).publishEvent(any(PasswordResetEvent.class));
    }

    @Test
    void testRequestPasswordReset_userNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> passwordResetService.requestPasswordReset("missing@example.com"));

        assertEquals(FeedBackMessage.NO_USER_FOUND + "missing@example.com", ex.getMessage());
    }

    @Test
    void testResetPassword_success() {
        User user = new User();
        String rawPassword = "newPassword";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);

        String result = passwordResetService.resetPassword(rawPassword, user);

        assertEquals(FeedBackMessage.PASSWORD_RESET_SUCCESS, result);
        assertEquals(encodedPassword, user.getPassword());
    }

    @Test
    void testResetPassword_failsDuringSave() {
        User user = new User();
        String rawPassword = "password";

        when(passwordEncoder.encode(rawPassword)).thenReturn("encoded");
        when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(rawPassword, user));

        assertEquals("DB error", ex.getMessage());
    }
}
