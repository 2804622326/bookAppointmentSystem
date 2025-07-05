package com.dailycodework.universalpetcare.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentUpdateRequestTest {

    @Test
    void testAppointmentUpdateRequestFields() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        request.setAppointmentDate("2025-06-01");
        request.setAppointmentTime("10:00");
        request.setReason("Follow-up");

        assertEquals("2025-06-01", request.getAppointmentDate());
        assertEquals("10:00", request.getAppointmentTime());
        assertEquals("Follow-up", request.getReason());
    }

    @Test
    void testAllArgsConstructor() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest("2025-06-01", "10:00", "Follow-up");

        assertEquals("2025-06-01", request.getAppointmentDate());
        assertEquals("10:00", request.getAppointmentTime());
        assertEquals("Follow-up", request.getReason());
    }

    @Test
    void testNoArgsConstructor() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();

        assertNull(request.getAppointmentDate());
        assertNull(request.getAppointmentTime());
        assertNull(request.getReason());
    }
}