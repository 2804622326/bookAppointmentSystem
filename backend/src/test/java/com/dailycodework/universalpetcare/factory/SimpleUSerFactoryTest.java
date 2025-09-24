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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleUSerFactoryTest {

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

    private RegistrationRequest buildRequest(String userType) {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("user@example.com");
        request.setPassword("password");
        request.setUserType(userType);
        return request;
    }

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode("password")).thenReturn("encoded");
    }

    @Test
    void createUser_whenTypeIsVet_returnsVeterinarian() {
        RegistrationRequest request = buildRequest("vet");
        Veterinarian veterinarian = new Veterinarian();

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(veterinarianFactory.createVeterinarian(any(RegistrationRequest.class))).thenReturn(veterinarian);

        User created = simpleUserFactory.createUser(request);

        verify(veterinarianFactory).createVeterinarian(request);
        assertThat(created).isSameAs(veterinarian);
        assertThat(request.getUserType()).isEqualTo("VET");
        assertThat(request.getPassword()).isEqualTo("encoded");
    }

    @Test
    void createUser_whenTypeIsPatient_returnsPatient() {
        RegistrationRequest request = buildRequest("patient");
        Patient patient = new Patient();

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(patientFactory.createPatient(any(RegistrationRequest.class))).thenReturn(patient);

        User created = simpleUserFactory.createUser(request);

        verify(patientFactory).createPatient(request);
        assertThat(created).isSameAs(patient);
        assertThat(request.getPassword()).isEqualTo("encoded");
    }

    @Test
    void createUser_whenTypeIsAdmin_returnsAdmin() {
        RegistrationRequest request = buildRequest("ADMIN");
        Admin admin = new Admin();

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(adminFactory.createAdmin(any(RegistrationRequest.class))).thenReturn(admin);

        User created = simpleUserFactory.createUser(request);

        verify(adminFactory).createAdmin(request);
        assertThat(created).isSameAs(admin);
        assertThat(request.getPassword()).isEqualTo("encoded");
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsAlreadyExistsException() {
        RegistrationRequest request = buildRequest("VET");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> simpleUserFactory.createUser(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("user@example.com");
    }

    @Test
    void createUser_whenTypeIsUnknown_throwsIllegalArgumentException() {
        RegistrationRequest request = buildRequest("UNKNOWN");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);

        assertThatThrownBy(() -> simpleUserFactory.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported user type");
    }

    @Test
    void createUser_whenTypeIsBlank_throwsIllegalArgumentException() {
        RegistrationRequest request = buildRequest("   ");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);

        assertThatThrownBy(() -> simpleUserFactory.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User type");
    }
}
