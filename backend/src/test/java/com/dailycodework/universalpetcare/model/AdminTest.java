package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testAdminFields() {
        Admin admin = new Admin();
        admin.setId(1L);

        assertEquals(1L, admin.getId());
    }

    @Test
    void testAllArgsConstructor() {
        Admin admin = new Admin(2L);
        assertEquals(2L, admin.getId());
    }
}