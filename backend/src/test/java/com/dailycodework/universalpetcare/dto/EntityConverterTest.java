package com.dailycodework.universalpetcare.dto;

import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityConverterTest {

    private EntityConverter<Appointment, AppointmentDto> converter;

    @BeforeEach
    void setUp() {
        converter = new EntityConverter<>(new ModelMapper());
    }

    @Test
    void testMapEntityToDto() {
        Appointment appointment = new Appointment();
        appointment.setId(100L);
        appointment.setAppointmentNo("APT-999");
        appointment.setAppointmentDate(LocalDate.of(2025, 5, 21));
        appointment.setAppointmentTime(LocalTime.of(14, 0));
        appointment.setReason("Annual Checkup");
        appointment.setStatus(AppointmentStatus.APPROVED);

        AppointmentDto dto = converter.mapEntityToDto(appointment, AppointmentDto.class);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals("APT-999", dto.getAppointmentNo());
        assertEquals(LocalDate.of(2025, 5, 21), dto.getAppointmentDate());
        assertEquals(LocalTime.of(14, 0), dto.getAppointmentTime());
        assertEquals("Annual Checkup", dto.getReason());
        assertEquals(AppointmentStatus.APPROVED, dto.getStatus());
    }

    @Test
    void testMapDtoToEntity() {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(101L);
        dto.setAppointmentNo("APT-888");
        dto.setAppointmentDate(LocalDate.of(2025, 6, 1));
        dto.setAppointmentTime(LocalTime.of(10, 0));
        dto.setReason("Follow-up");
        dto.setStatus(AppointmentStatus.UP_COMING);

        Appointment entity = converter.mapDtoToEntity(dto, Appointment.class);

        assertNotNull(entity);
        assertEquals(101L, entity.getId());
        assertEquals("APT-888", entity.getAppointmentNo());
        assertEquals(LocalDate.of(2025, 6, 1), entity.getAppointmentDate());
        assertEquals(LocalTime.of(10, 0), entity.getAppointmentTime());
        assertEquals("Follow-up", entity.getReason());
        assertEquals(AppointmentStatus.UP_COMING, entity.getStatus());
    }
}
