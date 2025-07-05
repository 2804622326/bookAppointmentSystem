package com.dailycodework.universalpetcare.service.token;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VerificationTokenRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationTokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @InjectMocks
    private VerificationTokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateToken_invalidToken() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        String result = tokenService.validateToken("invalid");

        assertEquals(FeedBackMessage.INVALID_TOKEN, result);
    }

    @Test
    void testValidateToken_alreadyVerified() {
        User user = new User();
        user.setEnabled(true);
        VerificationToken token = new VerificationToken();
        token.setUser(user);

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        String result = tokenService.validateToken("token");

        assertEquals(FeedBackMessage.TOKEN_ALREADY_VERIFIED, result);
    }

    @Test
    void testValidateToken_expiredToken() {
        User user = new User();
        user.setEnabled(false);
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setExpirationDate(new Date(System.currentTimeMillis() - 1000)); // expired

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        String result = tokenService.validateToken("token");

        assertEquals(FeedBackMessage.EXPIRED_TOKEN, result);
    }

    @Test
    void testValidateToken_validToken() {
        User user = new User();
        user.setEnabled(false);
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setExpirationDate(new Date(System.currentTimeMillis() + 10000)); // not expired

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));
        when(userRepository.save(user)).thenReturn(user);

        String result = tokenService.validateToken("token");

        assertEquals(FeedBackMessage.VALID_VERIFICATION_TOKEN, result);
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void testSaveVerificationTokenForUser() {
        User user = new User();
        String tokenStr = "token123";

        tokenService.saveVerificationTokenForUser(tokenStr, user);

        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(tokenRepository).save(captor.capture());

        VerificationToken savedToken = captor.getValue();
        assertEquals(tokenStr, savedToken.getToken());
        assertEquals(user, savedToken.getUser());
    }

    @Test
    void testGenerateNewVerificationToken_success() {
        VerificationToken oldToken = new VerificationToken();
        oldToken.setToken("oldtoken");
        oldToken.setExpirationDate(new Date());
        when(tokenRepository.findByToken("oldtoken")).thenReturn(Optional.of(oldToken));
        when(tokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        VerificationToken newToken = tokenService.generateNewVerificationToken("oldtoken");

        assertNotNull(newToken.getToken());
        assertNotEquals("oldtoken", newToken.getToken());
        verify(tokenRepository).save(newToken);
    }

    @Test
    void testGenerateNewVerificationToken_notFound() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> tokenService.generateNewVerificationToken("missing"));

        assertTrue(ex.getMessage().contains(FeedBackMessage.INVALID_VERIFICATION_TOKEN));
    }

    @Test
    void testFindByToken_found() {
        VerificationToken token = new VerificationToken();
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        Optional<VerificationToken> result = tokenService.findByToken("token");

        assertTrue(result.isPresent());
        assertEquals(token, result.get());
    }

    @Test
    void testFindByToken_notFound() {
        when(tokenRepository.findByToken("token")).thenReturn(Optional.empty());

        Optional<VerificationToken> result = tokenService.findByToken("token");

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteVerificationToken() {
        Long id = 123L;
        tokenService.deleteVerificationToken(id);

        verify(tokenRepository).deleteById(id);
    }

    @Test
    void testIsTokenExpired_expired() {
        VerificationToken token = new VerificationToken();
        Calendar past = Calendar.getInstance();
        past.add(Calendar.HOUR, -1);
        token.setExpirationDate(past.getTime());

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        assertTrue(tokenService.isTokenExpired("token"));
    }

    @Test
    void testIsTokenExpired_notExpired() {
        VerificationToken token = new VerificationToken();
        Calendar future = Calendar.getInstance();
        future.add(Calendar.HOUR, 1);
        token.setExpirationDate(future.getTime());

        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        assertFalse(tokenService.isTokenExpired("token"));
    }

    @Test
    void testIsTokenExpired_tokenNotFound() {
        when(tokenRepository.findByToken("token")).thenReturn(Optional.empty());

        assertTrue(tokenService.isTokenExpired("token"));
    }
}
