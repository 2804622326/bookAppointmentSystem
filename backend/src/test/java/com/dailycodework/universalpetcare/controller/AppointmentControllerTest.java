package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.event.AppointmentApprovedEvent;
import com.dailycodework.universalpetcare.event.AppointmentBookedEvent;
import com.dailycodework.universalpetcare.event.AppointmentDeclinedEvent;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.request.AppointmentUpdateRequest;
import com.dailycodework.universalpetcare.request.BookAppointmentRequest;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.appointment.AppointmentService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AppointmentController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAppointments_success() {
        List<Appointment> mockList = Collections.singletonList(new Appointment());
        when(appointmentService.getAllAppointments()).thenReturn(mockList);

        ResponseEntity<ApiResponse> response = controller.getAllAppointments();

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.APPOINTMENT_FOUND, response.getBody().getMessage());
        assertEquals(mockList, response.getBody().getData());
    }

    @Test
    void testGetAllAppointments_error() {
        when(appointmentService.getAllAppointments()).thenThrow(new RuntimeException("fail"));

        ResponseEntity<ApiResponse> response = controller.getAllAppointments();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("fail", response.getBody().getMessage());
    }

    @Test
    void testBookAppointment_success() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        Appointment mockAppointment = new Appointment();
        when(appointmentService.createAppointment(request, 1L, 2L)).thenReturn(mockAppointment);

        ResponseEntity<ApiResponse> response = controller.bookAppointment(request, 1L, 2L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockAppointment, response.getBody().getData());
        verify(publisher).publishEvent(any(AppointmentBookedEvent.class));
    }

    @Test
    void testBookAppointment_notFound() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        when(appointmentService.createAppointment(request, 1L, 2L))
                .thenThrow(new ResourceNotFoundException("user not found"));

        ResponseEntity<ApiResponse> response = controller.bookAppointment(request, 1L, 2L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("user not found", response.getBody().getMessage());
    }

    @Test
    void testGetAppointmentById_success() {
        Appointment appointment = new Appointment();
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        ResponseEntity<ApiResponse> response = controller.getAppointmentById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(appointment, response.getBody().getData());
    }

    @Test
    void testGetAppointmentById_notFound() {
        when(appointmentService.getAppointmentById(1L)).thenThrow(new ResourceNotFoundException("not found"));

        ResponseEntity<ApiResponse> response = controller.getAppointmentById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("not found", response.getBody().getMessage());
    }

    @Test
    void testDeleteAppointment_success() {
        ResponseEntity<ApiResponse> response = controller.deleteAppointmentById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.APPOINTMENT_DELETE_SUCCESS, response.getBody().getMessage());
        verify(appointmentService).deleteAppointment(1L);
    }

    @Test
    void testUpdateAppointment_success() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        Appointment updated = new Appointment();
        when(appointmentService.updateAppointment(1L, request)).thenReturn(updated);

        ResponseEntity<ApiResponse> response = controller.updateAppointment(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody().getData());
    }

    @Test
    void testCancelAppointment_illegalState() {
        when(appointmentService.cancelAppointment(1L))
                .thenThrow(new IllegalStateException("不能取消"));

        ResponseEntity<ApiResponse> response = controller.cancelAppointment(1L);

        assertEquals(406, response.getStatusCodeValue());
        assertEquals("不能取消", response.getBody().getMessage());
    }

    @Test
    void testApproveAppointment_success() {
        Appointment mock = new Appointment();
        when(appointmentService.approveAppointment(1L)).thenReturn(mock);

        ResponseEntity<ApiResponse> response = controller.approveAppointment(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mock, response.getBody().getData());
        verify(publisher).publishEvent(any(AppointmentApprovedEvent.class));
    }

    @Test
    void testDeclineAppointment_success() {
        Appointment mock = new Appointment();
        when(appointmentService.declineAppointment(1L)).thenReturn(mock);

        ResponseEntity<ApiResponse> response = controller.declineAppointment(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mock, response.getBody().getData());
        verify(publisher).publishEvent(any(AppointmentDeclinedEvent.class));
    }

    @Test
    void testCountAppointments() {
        when(appointmentService.countAppointment()).thenReturn(123L);

        long result = controller.countAppointments();

        assertEquals(123L, result);
    }

    @Test
    void testGetAppointmentSummary_success() {
        List<Map<String, Object>> summary = new ArrayList<>();
        when(appointmentService.getAppointmentSummary()).thenReturn(summary);

        ResponseEntity<ApiResponse> response = controller.getAppointmentSummary();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(summary, response.getBody().getData());
    }

    @Test
    void testGetAppointmentSummary_error() {
        when(appointmentService.getAppointmentSummary())
                .thenThrow(new RuntimeException("error"));

        ResponseEntity<ApiResponse> response = controller.getAppointmentSummary();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("error"));
    }
}
