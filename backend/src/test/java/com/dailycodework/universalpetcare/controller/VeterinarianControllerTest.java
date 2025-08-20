package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.UserDto;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.veterinarian.IVeterinarianService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeterinarianControllerTest {

    @Mock
    private IVeterinarianService veterinarianService;

    @InjectMocks
    private VeterinarianController veterinarianController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllVeterinarians() {
        List<UserDto> mockVets = Arrays.asList(new UserDto(), new UserDto());
        when(veterinarianService.getAllVeterinariansWithDetails()).thenReturn(mockVets);

        ResponseEntity<ApiResponse> response = veterinarianController.getAllVeterinarians();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, response.getBody().getMessage());
        assertEquals(mockVets, response.getBody().getData());
    }

    @Test
    void testSearchVeterinariansForAppointment_found() {
        List<UserDto> result = List.of(new UserDto());
        when(veterinarianService.findAvailableVetsForAppointment("dentistry", LocalDate.now(), LocalTime.NOON))
                .thenReturn(result);

        ResponseEntity<ApiResponse> response = veterinarianController
                .searchVeterinariansForAppointment(LocalDate.now(), LocalTime.NOON, "dentistry");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(result, response.getBody().getData());
    }

    @Test
    void testSearchVeterinariansForAppointment_noneFound() {
        when(veterinarianService.findAvailableVetsForAppointment("surgery", null, null))
                .thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse> response = veterinarianController
                .searchVeterinariansForAppointment(null, null, "surgery");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.NO_VETS_AVAILABLE, response.getBody().getMessage());
    }

    @Test
    void testSearchVeterinariansForAppointment_throwsNotFound() {
        when(veterinarianService.findAvailableVetsForAppointment("invalid", null, null))
                .thenThrow(new ResourceNotFoundException("No vets"));

        ResponseEntity<ApiResponse> response = veterinarianController
                .searchVeterinariansForAppointment(null, null, "invalid");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("No vets", response.getBody().getMessage());
    }

    @Test
    void testGetAllSpecializations_success() {
        List<String> specializations = List.of("Dentistry", "Surgery");
        when(veterinarianService.getSpecializations()).thenReturn(specializations);

        ResponseEntity<ApiResponse> response = veterinarianController.getAllSpecializations();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, response.getBody().getMessage());
        assertEquals(specializations, response.getBody().getData());
    }

    @Test
    void testGetAllSpecializations_exception() {
        when(veterinarianService.getSpecializations()).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<ApiResponse> response = veterinarianController.getAllSpecializations();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Service error", response.getBody().getMessage());
    }

    @Test
    void testAggregateVetsBySpecialization() {
        List<Map<String, Object>> aggregation = List.of(
                Map.of("specialization", "Dentistry", "count", 3),
                Map.of("specialization", "Surgery", "count", 2)
        );

        when(veterinarianService.aggregateVetsBySpecialization()).thenReturn(aggregation);

        ResponseEntity<List<Map<String, Object>>> response = veterinarianController.aggregateVetsBySpecialization();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(aggregation, response.getBody());
    }
}
