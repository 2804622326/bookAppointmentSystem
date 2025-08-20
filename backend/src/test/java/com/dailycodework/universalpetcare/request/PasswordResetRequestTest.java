package com.dailycodework.universalpetcare.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestTest {

    @Test
    void testPasswordResetRequestFields() {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setToken("reset-token-123");
        request.setNewPassword("newSecurePassword");

        assertEquals("reset-token-123", request.getToken());
        assertEquals("newSecurePassword", request.getNewPassword());
    }
}