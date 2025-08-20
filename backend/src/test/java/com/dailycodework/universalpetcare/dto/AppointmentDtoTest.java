package com.dailycodework.universalpetcare.dto;

import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.model.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentDtoTest {

    @Test
    void testAppointmentDtoMapping() {
        // Mock appointment entity
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentDate(LocalDate.of(2024, 5, 20));
        appointment.setAppointmentTime(LocalTime.of(10, 30));
        appointment.setAppointmentNo("APT-001");
        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setReason("Vaccination");

        // Manual mapping (or from ModelMapper in real use case)
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        dto.setAppointmentNo(appointment.getAppointmentNo());
        dto.setReason(appointment.getReason());

        // Assertions
        assertEquals(1L, dto.getId());
        assertEquals("APT-001", dto.getAppointmentNo());
        assertEquals(AppointmentStatus.APPROVED, dto.getStatus());
        assertEquals("Vaccination", dto.getReason());
        assertEquals(LocalDate.of(2024, 5, 20), dto.getAppointmentDate());
        assertEquals(LocalTime.of(10, 30), dto.getAppointmentTime());
    }
}
