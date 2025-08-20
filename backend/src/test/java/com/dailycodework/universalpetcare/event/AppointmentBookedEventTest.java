package com.dailycodework.universalpetcare.event;

import com.dailycodework.universalpetcare.model.Appointment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentBookedEventTest {

    @Test
    void testAppointmentBookedEvent() {
        Appointment appointment = new Appointment();
        AppointmentBookedEvent event = new AppointmentBookedEvent(appointment);

        assertEquals(appointment, event.getAppointment());
        assertEquals(appointment, event.getSource());
    }
}