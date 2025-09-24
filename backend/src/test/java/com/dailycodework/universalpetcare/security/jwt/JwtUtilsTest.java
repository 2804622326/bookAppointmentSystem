package com.dailycodework.universalpetcare.security.jwt;

import com.dailycodework.universalpetcare.security.user.UPCUserDetails;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String secret = "YW55IGNhcm5hbCBwbGVhc3VyZSB5b3Ugd2FudCBmb3IgdGVzdGluZw=="; // base64 for test
    private final int expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        jwtUtils.jwtSecret = secret;
        jwtUtils.jwtExpirationMs = expirationMs;
    }

    @Test
    void testGenerateTokenAndParseUsername_success() {
        UPCUserDetails user = new UPCUserDetails(
                1L,
                "user@example.com",
                "password",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        String token = jwtUtils.generateTokenForUser(authentication);
        assertNotNull(token);

        String username = jwtUtils.getUserNameFromToken(token);
        assertEquals("user@example.com", username);
    }

    @Test
    void testValidateToken_valid() {
        UPCUserDetails userDetails = new UPCUserDetails(
                2L,
                "valid@example.com",
                "secret",
                true, // isEnabled 设置为 true 以模拟已启用账户
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String token = jwtUtils.generateTokenForUser(authentication);

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_invalidToken_shouldThrowException() {
        String invalidToken = "invalid.token.value";
        JwtException exception = assertThrows(JwtException.class, () -> jwtUtils.validateToken(invalidToken));
        assertNotNull(exception.getMessage());
    }

    @Test
    void testGetUserNameFromInvalidToken_shouldThrowException() {
        String invalidToken = "malformed.token.value";
        assertThrows(JwtException.class, () -> jwtUtils.getUserNameFromToken(invalidToken));
    }

    @Test
    void testValidateToken_expiredTokenThrowsException() {
        UPCUserDetails userDetails = new UPCUserDetails(
                3L,
                "expired@example.com",
                "secret",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_VET"))
        );
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        jwtUtils.jwtExpirationMs = -1000; // make the token expire immediately

        String token = jwtUtils.generateTokenForUser(authentication);

        assertThrows(JwtException.class, () -> jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_withDifferentSigningKeyThrowsException() {
        UPCUserDetails userDetails = new UPCUserDetails(
                4L,
                "sign@example.com",
                "secret",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateTokenForUser(authentication);

        JwtUtils other = new JwtUtils();
        other.jwtSecret = "c2Vjb25kLXJhbmRvbS1zZWNyZXQtZm9yLXRlc3Rpbmc="; // base64 for "second-random-secret-for-testing"
        other.jwtExpirationMs = expirationMs;

        assertThrows(JwtException.class, () -> other.validateToken(token));
    }
}
