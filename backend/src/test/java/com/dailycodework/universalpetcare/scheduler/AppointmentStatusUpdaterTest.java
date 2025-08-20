package com.dailycodework.universalpetcare.scheduler;

import com.dailycodework.universalpetcare.service.appointment.IAppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.mockito.Mockito.*;

class AppointmentStatusUpdaterTest {

    @Mock
    private IAppointmentService appointmentService;

    @InjectMocks
    private AppointmentStatusUpdater updater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutomateAppointmentStatusUpdate() {
        List<Long> mockIds = List.of(1L, 2L, 3L);
        when(appointmentService.getAppointmentIds()).thenReturn(mockIds);

        updater.automateAppointmentStatusUpdate();

        verify(appointmentService).getAppointmentIds();
        verify(appointmentService, times(1)).setAppointmentStatus(1L);
        verify(appointmentService, times(1)).setAppointmentStatus(2L);
        verify(appointmentService, times(1)).setAppointmentStatus(3L);
    }
}