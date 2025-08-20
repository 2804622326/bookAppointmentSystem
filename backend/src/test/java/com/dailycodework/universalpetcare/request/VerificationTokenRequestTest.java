package com.dailycodework.universalpetcare.request;

import com.dailycodework.universalpetcare.model.User;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class VerificationTokenRequestTest {

    @Test
    void testVerificationTokenRequestFields() {
        String token = "verify123";
        Date expiration = new Date();
        User user = new User();

        VerificationTokenRequest request = new VerificationTokenRequest();
        request.setToken(token);
        request.setExpirationTime(expiration);
        request.setUser(user);

        assertEquals(token, request.getToken());
        assertEquals(expiration, request.getExpirationTime());
        assertEquals(user, request.getUser());
    }
}