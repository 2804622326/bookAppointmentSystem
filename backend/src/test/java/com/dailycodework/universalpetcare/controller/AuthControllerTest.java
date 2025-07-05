package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.event.RegistrationCompleteEvent;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.request.LoginRequest;
import com.dailycodework.universalpetcare.request.PasswordResetRequest;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.response.JwtResponse;
import com.dailycodework.universalpetcare.security.jwt.JwtUtils;
import com.dailycodework.universalpetcare.security.user.UPCUserDetails;
import com.dailycodework.universalpetcare.service.password.PasswordResetService;
import com.dailycodework.universalpetcare.service.token.VerificationTokenService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private VerificationTokenService tokenService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("pass");

        UPCUserDetails userDetails = mock(UPCUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateTokenForUser(authentication)).thenReturn("mockJwt");

        ResponseEntity<ApiResponse> response = controller.login(request);

        assertEquals(200, response.getStatusCodeValue());
        JwtResponse jwtResponse = (JwtResponse) response.getBody().getData();
        assertEquals(1L, jwtResponse.getId());
        assertEquals("mockJwt", jwtResponse.getToken());
    }

    @Test
    void testLogin_disabledUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("disabled@example.com");
        request.setPassword("pass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("Disabled"));

        ResponseEntity<ApiResponse> response = controller.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.ACCOUNT_DISABLED, response.getBody().getMessage());
    }

    @Test
    void testLogin_invalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@example.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationServiceException("Bad credentials"));

        ResponseEntity<ApiResponse> response = controller.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Bad credentials", response.getBody().getMessage());
        assertEquals(FeedBackMessage.INVALID_PASSWORD, response.getBody().getData());
    }

    @Test
    void testVerifyEmail_valid() {
        when(tokenService.validateToken("token123")).thenReturn("VALID");

        ResponseEntity<ApiResponse> response = controller.verifyEmail("token123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.VALID_VERIFICATION_TOKEN, response.getBody().getMessage());
    }

    @Test
    void testResendVerificationToken_success() {
        VerificationToken token = mock(VerificationToken.class);
        User user = new User();
        when(token.getUser()).thenReturn(user);
        when(tokenService.generateNewVerificationToken("oldToken")).thenReturn(token);

        ResponseEntity<ApiResponse> response = controller.resendVerificationToken("oldToken");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.NEW_VERIFICATION_TOKEN_SENT, response.getBody().getMessage());
        verify(publisher).publishEvent(any(RegistrationCompleteEvent.class));
    }

    @Test
    void testRequestPasswordReset_success() {
        ResponseEntity<ApiResponse> response = controller.requestPasswordReset(Map.of("email", "user@example.com"));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PASSWORD_RESET_EMAIL_SENT, response.getBody().getMessage());
        verify(passwordResetService).requestPasswordReset("user@example.com");
    }

    @Test
    void testRequestPasswordReset_invalidEmail() {
        ResponseEntity<ApiResponse> response = controller.requestPasswordReset(Map.of());

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.INVALID_EMAIL, response.getBody().getMessage());
    }

    @Test
    void testRequestPasswordReset_userNotFound() {
        doThrow(new ResourceNotFoundException("No user"))
                .when(passwordResetService).requestPasswordReset("a@example.com");

        ResponseEntity<ApiResponse> response = controller.requestPasswordReset(Map.of("email", "a@example.com"));

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No user", response.getBody().getMessage());
    }

    @Test
    void testResetPassword_success() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken("token123");
        request.setNewPassword("newpass");

        User user = new User();

        when(passwordResetService.findUserByPasswordResetToken("token123"))
                .thenReturn(Optional.of(user));
        when(passwordResetService.resetPassword("newpass", user)).thenReturn("Password changed");

        ResponseEntity<ApiResponse> response = controller.resetPassword(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password changed", response.getBody().getMessage());
    }

    @Test
    void testResetPassword_missingInput() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken("");
        request.setNewPassword("");

        ResponseEntity<ApiResponse> response = controller.resetPassword(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.MISSING_PASSWORD, response.getBody().getMessage());
    }

    @Test
    void testResetPassword_tokenInvalid() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken("invalid");
        request.setNewPassword("newpass");

        when(passwordResetService.findUserByPasswordResetToken("invalid"))
                .thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = controller.resetPassword(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.INVALID_RESET_TOKEN, response.getBody().getMessage());
    }
}
