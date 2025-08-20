package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.UserDto;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.patient.IPatientService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientControllerTest {

    @Mock
    private IPatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPatients_success() {
        UserDto patient1 = new UserDto();
        UserDto patient2 = new UserDto();
        List<UserDto> mockPatients = Arrays.asList(patient1, patient2);

        when(patientService.getPatients()).thenReturn(mockPatients);

        ResponseEntity<ApiResponse> response = patientController.getAllPatients();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, response.getBody().getMessage());
        assertEquals(mockPatients, response.getBody().getData());
    }
}
