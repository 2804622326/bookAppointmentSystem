package com.dailycodework.universalpetcare.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordRequestTest {

    @Test
    void testChangePasswordRequestFields() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPass123");
        request.setNewPassword("newPass456");
        request.setConfirmNewPassword("newPass456");

        assertEquals("oldPass123", request.getCurrentPassword());
        assertEquals("newPass456", request.getNewPassword());
        assertEquals("newPass456", request.getConfirmNewPassword());
    }
}