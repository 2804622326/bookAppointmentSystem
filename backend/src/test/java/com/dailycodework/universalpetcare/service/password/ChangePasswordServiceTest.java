package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangePasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ChangePasswordService changePasswordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChangePassword_success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("old123");
        request.setNewPassword("new123");
        request.setConfirmNewPassword("new123");

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOld");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old123", "encodedOld")).thenReturn(true);
        when(passwordEncoder.encode("new123")).thenReturn("encodedNew");

        changePasswordService.changePassword(1L, request);

        assertEquals("encodedNew", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePassword_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ChangePasswordRequest request = new ChangePasswordRequest();
        Exception ex = assertThrows(ResourceNotFoundException.class,
                () -> changePasswordService.changePassword(1L, request));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testChangePassword_emptyFields() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("");
        request.setNewPassword("");

        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(1L, request));
        assertEquals("All fields are required", ex.getMessage());
    }

    @Test
    void testChangePassword_wrongCurrentPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("new123");

        User user = new User();
        user.setPassword("encodedCorrect");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedCorrect")).thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(1L, request));
        assertEquals("Current password does not match", ex.getMessage());
    }

    @Test
    void testChangePassword_passwordMismatch() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("current");
        request.setNewPassword("new123");
        request.setConfirmNewPassword("notMatch");

        User user = new User();
        user.setPassword("encodedCurrent");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current", "encodedCurrent")).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> changePasswordService.changePassword(1L, request));
        assertEquals("Password confirmation mis-match ", ex.getMessage());
    }
}
