package com.dailycodework.universalpetcare.service.patient;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDto;
import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private EntityConverter<User, UserDto> entityConverter;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPatients_returnsUserDtos() {
        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        UserDto dto1 = new UserDto();
        UserDto dto2 = new UserDto();

        when(patientRepository.findAll()).thenReturn(Arrays.asList(patient1, patient2));
        when(entityConverter.mapEntityToDto(patient1, UserDto.class)).thenReturn(dto1);
        when(entityConverter.mapEntityToDto(patient2, UserDto.class)).thenReturn(dto2);

        List<UserDto> result = patientService.getPatients();

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(patientRepository).findAll();
        verify(entityConverter, times(2)).mapEntityToDto(any(), eq(UserDto.class));
    }
}
