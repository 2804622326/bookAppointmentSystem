package com.dailycodework.universalpetcare.event;

import com.dailycodework.universalpetcare.model.Appointment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentDeclinedEventTest {

    @Test
    void testAppointmentDeclinedEvent() {
        Appointment appointment = new Appointment();
        AppointmentDeclinedEvent event = new AppointmentDeclinedEvent(appointment);

        assertEquals(appointment, event.getAppointment());
        assertEquals(appointment, event.getSource());
    }
}