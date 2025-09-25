package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
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
class VeterinarianFactoryTest {

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Mock
    private UserAttributesMapper userAttributesMapper;

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private VeterinarianFactory veterinarianFactory;

    private RegistrationRequest registrationRequest;
    private Veterinarian veterinarian;
    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("Dr. Jane");
        registrationRequest.setLastName("Smith");
        registrationRequest.setEmail("dr.jane@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setGender("Female");
        registrationRequest.setPhoneNumber("9876543210");
        registrationRequest.setUserType("VET");
        registrationRequest.setSpecialization("Cardiology");

        veterinarian = new Veterinarian();
        roles = Set.of(new Role("VET"));
    }

    @Test
    void testCreateVeterinarian_Success() {
        // Given
        when(roleService.setUserRole("VET")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(veterinarian);

        // When
        Veterinarian result = veterinarianFactory.createVeterinarian(registrationRequest);

        // Then
        assertNotNull(result);
        verify(roleService).setUserRole("VET");
        verify(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
        verify(veterinarianRepository).save(any(Veterinarian.class));
    }

    @Test
    void testCreateVeterinarian_SetsCorrectRole() {
        // Given
        when(roleService.setUserRole("VET")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(veterinarian);

        // When
        veterinarianFactory.createVeterinarian(registrationRequest);

        // Then
        verify(roleService).setUserRole("VET");
        
        // Verify that setRoles is called with the correct roles
        ArgumentCaptor<Veterinarian> vetCaptor = ArgumentCaptor.forClass(Veterinarian.class);
        verify(veterinarianRepository).save(vetCaptor.capture());
        
        Veterinarian capturedVet = vetCaptor.getValue();
        assertEquals(roles, capturedVet.getRoles());
    }

    @Test
    void testCreateVeterinarian_SetsSpecialization() {
        // Given
        when(roleService.setUserRole("VET")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(veterinarian);

        // When
        veterinarianFactory.createVeterinarian(registrationRequest);

        // Then
        ArgumentCaptor<Veterinarian> vetCaptor = ArgumentCaptor.forClass(Veterinarian.class);
        verify(veterinarianRepository).save(vetCaptor.capture());
        
        Veterinarian capturedVet = vetCaptor.getValue();
        assertEquals("Cardiology", capturedVet.getSpecialization());
    }

    @Test
    void testCreateVeterinarian_CallsUserAttributesMapper() {
        // Given
        when(roleService.setUserRole("VET")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(veterinarian);

        // When
        veterinarianFactory.createVeterinarian(registrationRequest);

        // Then
        verify(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
    }

    @Test
    void testCreateVeterinarian_SavesToRepository() {
        // Given
        when(roleService.setUserRole("VET")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(veterinarian);

        // When
        veterinarianFactory.createVeterinarian(registrationRequest);

        // Then
        verify(veterinarianRepository).save(any(Veterinarian.class));
    }

    @Test
    void testCreateVeterinarian_WithNullSpecialization() {
        // Given
        registrationRequest.setSpecialization(null);
        when(roleService.setUserRole("VET")).thenReturn(roles);
        doNothing().when(userAttributesMapper).setCommonAttributes(any(RegistrationRequest.class), any(Veterinarian.class));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(veterinarian);

        // When
        veterinarianFactory.createVeterinarian(registrationRequest);

        // Then
        ArgumentCaptor<Veterinarian> vetCaptor = ArgumentCaptor.forClass(Veterinarian.class);
        verify(veterinarianRepository).save(vetCaptor.capture());
        
        Veterinarian capturedVet = vetCaptor.getValue();
        assertNull(capturedVet.getSpecialization());
    }
}