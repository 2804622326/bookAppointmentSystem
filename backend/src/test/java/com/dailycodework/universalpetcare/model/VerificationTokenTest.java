package com.dailycodework.universalpetcare.model;

import com.dailycodework.universalpetcare.utils.SystemUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class VerificationTokenTest {

    @Test
    void testVerificationTokenFields() {
        VerificationToken token = new VerificationToken();
        User user = new User();
        Date expiration = new Date();

        token.setId(1L);
        token.setToken("abc123");
        token.setExpirationDate(expiration);
        token.setUser(user);

        assertEquals(1L, token.getId());
        assertEquals("abc123", token.getToken());
        assertEquals(expiration, token.getExpirationDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testConstructorWithTokenAndUser() {
        User user = new User();
        VerificationToken token = new VerificationToken("xyz456", user);

        assertEquals("xyz456", token.getToken());
        assertEquals(user, token.getUser());
        assertNotNull(token.getExpirationDate());
        assertEquals(SystemUtils.getExpirationTime().toString().substring(0, 10),
                token.getExpirationDate().toString().substring(0, 10));
    }
}