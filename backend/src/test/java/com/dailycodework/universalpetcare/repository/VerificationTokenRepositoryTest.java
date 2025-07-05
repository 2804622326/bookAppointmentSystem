package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.VerificationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationTokenRepositoryTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @InjectMocks
    private DummyTokenCaller dummyTokenCaller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByToken_TokenExists() {
        String tokenValue = "abc123";
        VerificationToken token = new VerificationToken();
        token.setToken(tokenValue);

        when(tokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        Optional<VerificationToken> result = dummyTokenCaller.findToken(tokenValue);

        assertTrue(result.isPresent());
        assertEquals(tokenValue, result.get().getToken());

        verify(tokenRepository).findByToken(tokenValue);
    }

    @Test
    void testFindByToken_TokenNotFound() {
        String tokenValue = "notfound";

        when(tokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        Optional<VerificationToken> result = dummyTokenCaller.findToken(tokenValue);

        assertFalse(result.isPresent());

        verify(tokenRepository).findByToken(tokenValue);
    }

    static class DummyTokenCaller {
        @InjectMocks
        private VerificationTokenRepository tokenRepository;

        public Optional<VerificationToken> findToken(String token) {
            return tokenRepository.findByToken(token);
        }
    }
}