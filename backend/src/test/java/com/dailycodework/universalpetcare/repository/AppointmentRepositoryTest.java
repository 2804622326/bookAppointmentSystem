package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.Veterinarian;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentRepositoryTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Test
    public void testFindByAppointmentNo() {
        String appointmentNo = "12345";
        Appointment appointment = new Appointment();
        when(appointmentRepository.findByAppointmentNo(appointmentNo)).thenReturn(appointment);

        Appointment result = appointmentRepository.findByAppointmentNo(appointmentNo);

        assertNotNull(result);
        assertEquals(appointment, result);
        verify(appointmentRepository, times(1)).findByAppointmentNo(appointmentNo);
    }

    @Test
    public void testExistsByVeterinarianIdAndPatientIdAndStatus() {
        Long veterinarianId = 1L;
        Long reviewerId = 2L;
        AppointmentStatus appointmentStatus = AppointmentStatus.COMPLETED;
        when(appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, appointmentStatus)).thenReturn(true);

        boolean result = appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, appointmentStatus);

        assertTrue(result);
        verify(appointmentRepository, times(1)).existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, appointmentStatus);
    }

    @Test
    public void testFindAllByUserId() {
        Long userId = 1L;
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment());
        when(appointmentRepository.findAllByUserId(userId)).thenReturn(appointments);

        List<Appointment> result = appointmentRepository.findAllByUserId(userId);

        assertNotNull(result);
        assertEquals(appointments.size(), result.size());
        verify(appointmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void testFindByVeterinarianAndAppointmentDate() {
        User veterinarian = new Veterinarian();
        LocalDate requestedDate = LocalDate.now();
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment());
        when(appointmentRepository.findByVeterinarianAndAppointmentDate(veterinarian, requestedDate)).thenReturn(appointments);

        List<Appointment> result = appointmentRepository.findByVeterinarianAndAppointmentDate(veterinarian, requestedDate);

        assertNotNull(result);
        assertEquals(appointments.size(), result.size());
        verify(appointmentRepository, times(1)).findByVeterinarianAndAppointmentDate(veterinarian, requestedDate);
    }
}