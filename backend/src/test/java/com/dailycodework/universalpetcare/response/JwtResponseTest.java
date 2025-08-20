package com.dailycodework.universalpetcare.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtResponseTest {

    @Test
    void testJwtResponseAllArgsConstructor() {
        JwtResponse response = new JwtResponse(1L, "jwt-token-123");

        assertEquals(1L, response.getId());
        assertEquals("jwt-token-123", response.getToken());
    }

    @Test
    void testJwtResponseNoArgsConstructorAndSetters() {
        JwtResponse response = new JwtResponse();
        response.setId(2L);
        response.setToken("jwt-token-456");

        assertEquals(2L, response.getId());
        assertEquals("jwt-token-456", response.getToken());
    }
}