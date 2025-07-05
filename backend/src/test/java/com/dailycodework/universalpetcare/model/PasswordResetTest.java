package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTest {

    @Test
    void testPasswordResetFields() {
        PasswordReset reset = new PasswordReset();
        User user = new User();
        Date expiration = new Date();

        reset.setId(1L);
        reset.setToken("reset-token-abc");
        reset.setExpirationTime(expiration);
        reset.setUser(user);

        assertEquals(1L, reset.getId());
        assertEquals("reset-token-abc", reset.getToken());
        assertEquals(expiration, reset.getExpirationTime());
        assertEquals(user, reset.getUser());
    }
}