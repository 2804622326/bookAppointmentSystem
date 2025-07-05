package com.dailycodework.universalpetcare.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentStatusTest {

    @Test
    void testEnumValues() {
        assertEquals(AppointmentStatus.CANCELLED, AppointmentStatus.valueOf("CANCELLED"));
        assertEquals(AppointmentStatus.ON_GOING, AppointmentStatus.valueOf("ON_GOING"));
        assertEquals(AppointmentStatus.UP_COMING, AppointmentStatus.valueOf("UP_COMING"));
        assertEquals(AppointmentStatus.APPROVED, AppointmentStatus.valueOf("APPROVED"));
        assertEquals(AppointmentStatus.NOT_APPROVED, AppointmentStatus.valueOf("NOT_APPROVED"));
        assertEquals(AppointmentStatus.WAITING_FOR_APPROVAL, AppointmentStatus.valueOf("WAITING_FOR_APPROVAL"));
        assertEquals(AppointmentStatus.PENDING, AppointmentStatus.valueOf("PENDING"));
        assertEquals(AppointmentStatus.COMPLETED, AppointmentStatus.valueOf("COMPLETED"));
    }

    @Test
    void testEnumCount() {
        AppointmentStatus[] values = AppointmentStatus.values();
        assertEquals(8, values.length);
    }
}