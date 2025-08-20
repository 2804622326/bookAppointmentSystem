package com.dailycodework.universalpetcare.event;

import com.dailycodework.universalpetcare.model.Appointment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentApprovedEventTest {

    @Test
    void testAppointmentApprovedEvent() {
        Appointment appointment = new Appointment();
        AppointmentApprovedEvent event = new AppointmentApprovedEvent(appointment);

        assertEquals(appointment, event.getAppointment());
        assertEquals(appointment, event.getSource());
    }
}