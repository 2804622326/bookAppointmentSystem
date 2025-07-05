package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.VerificationTokenRequest;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.token.IVerificationTokenService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationTokenControllerTest {

    @Mock
    private IVerificationTokenService verificationTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VerificationTokenController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateToken_valid() {
        when(verificationTokenService.validateToken("abc")).thenReturn("VALID");

        ResponseEntity<ApiResponse> response = controller.validateToken("abc");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.VALID_VERIFICATION_TOKEN, response.getBody().getMessage());
    }

    @Test
    void testValidateToken_expired() {
        when(verificationTokenService.validateToken("expired")).thenReturn("EXPIRED");

        ResponseEntity<ApiResponse> response = controller.validateToken("expired");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.EXPIRED_TOKEN, response.getBody().getMessage());
    }

    @Test
    void testCheckTokenExpiration_expired() {
        when(verificationTokenService.isTokenExpired("xyz")).thenReturn(true);

        ResponseEntity<ApiResponse> response = controller.checkTokenExpiration("xyz");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.EXPIRED_TOKEN, response.getBody().getMessage());
    }

    @Test
    void testCheckTokenExpiration_valid() {
        when(verificationTokenService.isTokenExpired("xyz")).thenReturn(false);

        ResponseEntity<ApiResponse> response = controller.checkTokenExpiration("xyz");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.VALID_VERIFICATION_TOKEN, response.getBody().getMessage());
    }

    @Test
    void testSaveVerificationTokenForUser_success() {
        User user = new User();
        user.setId(1L);
        VerificationTokenRequest request = new VerificationTokenRequest();
        request.setToken("token123");
        request.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = controller.saveVerificationTokenForUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.TOKEN_SAVED_SUCCESS, response.getBody().getMessage());
        verify(verificationTokenService).saveVerificationTokenForUser("token123", user);
    }

    @Test
    void testSaveVerificationTokenForUser_userNotFound() {
        VerificationTokenRequest request = new VerificationTokenRequest();
        User user = new User();
        user.setId(99L);
        request.setUser(user);
        request.setToken("abc");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> controller.saveVerificationTokenForUser(request));
        assertEquals(FeedBackMessage.USER_FOUND, ex.getMessage());
    }

    @Test
    void testGenerateNewVerificationToken() {
        VerificationToken token = new VerificationToken();
        token.setToken("new-token");

        when(verificationTokenService.generateNewVerificationToken("old-token")).thenReturn(token);

        ResponseEntity<ApiResponse> response = controller.generateNewVerificationToken("old-token");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(token, response.getBody().getData());
    }

    @Test
    void testDeleteUserToken() {
        ResponseEntity<ApiResponse> response = controller.deleteUserToken(77L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.TOKEN_DELETE_SUCCESS, response.getBody().getMessage());
        verify(verificationTokenService).deleteVerificationToken(77L);
    }
}
