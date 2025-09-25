package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.repository.PatientRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.service.user.UserAttributesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientFactoryTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserAttributesMapper userAttributesMapper;

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private PatientFactory patientFactory;

    private RegistrationRequest registrationRequest;
    private Patient patient;
    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("John");
        registrationRequest.setLastName("Doe");
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setGender("Male");
        registrationRequest.setPhoneNumber("1234567890");
        registrationRequest.setUserType("PATIENT");

        patient = new Patient();
        roles = Set.of(new Role("PATIENT"));
    }

    @Test
    void testCreatePatient_Success() {
        // Given
        when(roleService.setUserRole("PATIENT")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Patient.class));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        Patient result = patientFactory.createPatient(registrationRequest);

        // Then
        assertNotNull(result);
        verify(roleService).setUserRole("PATIENT");
        verify(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Patient.class));
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testCreatePatient_SetsCorrectRole() {
        // Given
        when(roleService.setUserRole("PATIENT")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Patient.class));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        Patient result = patientFactory.createPatient(registrationRequest);

        // Then
        verify(roleService).setUserRole("PATIENT");
        
        // Verify that setRoles is called with the correct roles
        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(patientCaptor.capture());
        
        Patient capturedPatient = patientCaptor.getValue();
        assertEquals(roles, capturedPatient.getRoles());
    }

    @Test
    void testCreatePatient_CallsUserAttributesMapper() {
        // Given
        when(roleService.setUserRole("PATIENT")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Patient.class));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        patientFactory.createPatient(registrationRequest);

        // Then
        verify(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Patient.class));
    }

    @Test
    void testCreatePatient_SavesToRepository() {
        // Given
        when(roleService.setUserRole("PATIENT")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Patient.class));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        patientFactory.createPatient(registrationRequest);

        // Then
        verify(patientRepository).save(any(Patient.class));
    }
}