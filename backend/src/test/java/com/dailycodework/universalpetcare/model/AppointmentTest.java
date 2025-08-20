package com.dailycodework.universalpetcare.model;

import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    @Test
    void testAppointmentFields() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setReason("Checkup");
        appointment.setAppointmentDate(LocalDate.of(2025, 6, 1));
        appointment.setAppointmentTime(LocalTime.of(10, 0));
        appointment.setAppointmentNo("1234567890");
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPets(List.of());

        assertEquals(1L, appointment.getId());
        assertEquals("Checkup", appointment.getReason());
        assertEquals(LocalDate.of(2025, 6, 1), appointment.getAppointmentDate());
        assertEquals(LocalTime.of(10, 0), appointment.getAppointmentTime());
        assertEquals("1234567890", appointment.getAppointmentNo());
        assertEquals(AppointmentStatus.PENDING, appointment.getStatus());
        assertNotNull(appointment.getPets());
    }

    @Test
    void testAddPatient() {
        Appointment appointment = new Appointment();
        User user = new User();

        appointment.addPatient(user);

        assertEquals(user, appointment.getPatient());
        assertTrue(user.getAppointments().contains(appointment));
    }

    @Test
    void testAddVeterinarian() {
        Appointment appointment = new Appointment();
        User vet = new User();

        appointment.addVeterinarian(vet);

        assertEquals(vet, appointment.getVeterinarian());
        assertTrue(vet.getAppointments().contains(appointment));
    }

    @Test
    void testSetAppointmentNo() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentNo();

        assertNotNull(appointment.getAppointmentNo());
        assertEquals(10, appointment.getAppointmentNo().length());
    }
}