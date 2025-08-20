package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.exception.AlreadyExistsException;
import com.dailycodework.universalpetcare.model.Admin;
import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleUserFactoryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VeterinarianFactory veterinarianFactory;

    @Mock
    private PatientFactory patientFactory;

    @Mock
    private AdminFactory adminFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SimpleUSerFactory simpleUserFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Vet() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("vet@example.com");
        request.setPassword("pass");
        request.setUserType("VET");

        Veterinarian vet = new Veterinarian();

        when(userRepository.existsByEmail("vet@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(veterinarianFactory.createVeterinarian(request)).thenReturn(vet);

        User result = simpleUserFactory.createUser(request);

        verify(veterinarianFactory).createVeterinarian(request);
        assertEquals(vet, result);
    }

    @Test
    void testCreateUser_Patient() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("patient@example.com");
        request.setPassword("pass");
        request.setUserType("PATIENT");

        Patient patient = new Patient();

        when(userRepository.existsByEmail("patient@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(patientFactory.createPatient(request)).thenReturn(patient);

        User result = simpleUserFactory.createUser(request);

        verify(patientFactory).createPatient(request);
        assertEquals(patient, result);
    }

    @Test
    void testCreateUser_Admin() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("admin@example.com");
        request.setPassword("pass");
        request.setUserType("ADMIN");

        Admin admin = new Admin();

        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(adminFactory.createAdmin(request)).thenReturn(admin);

        User result = simpleUserFactory.createUser(request);

        verify(adminFactory).createAdmin(request);
        assertEquals(admin, result);
    }

    @Test
    void testCreateUser_EmailExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("duplicate@example.com");

        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> simpleUserFactory.createUser(request));
    }

    @Test
    void testCreateUser_InvalidType() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("user@example.com");
        request.setPassword("pass");
        request.setUserType("UNKNOWN");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        User result = simpleUserFactory.createUser(request);

        assertNull(result);
    }
}