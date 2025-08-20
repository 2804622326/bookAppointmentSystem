package com.dailycodework.universalpetcare.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testLoginRequestFields() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("securePassword");

        assertEquals("user@example.com", request.getEmail());
        assertEquals("securePassword", request.getPassword());
    }
}