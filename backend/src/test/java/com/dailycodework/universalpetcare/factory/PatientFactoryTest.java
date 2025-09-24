package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.repository.PatientRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import com.dailycodework.universalpetcare.service.user.UserAttributesMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void createPatient_whenCalled_assignsRolesMapsAttributesAndPersists() {
        RegistrationRequest request = new RegistrationRequest();
        Patient persisted = new Patient();
        Set<Role> roles = Set.of(new Role());

        when(roleService.setUserRole("PATIENT")).thenReturn(roles);
        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        when(patientRepository.save(captor.capture())).thenReturn(persisted);

        Patient result = patientFactory.createPatient(request);

        verify(roleService).setUserRole("PATIENT");
        verify(userAttributesMapper).setCommonAttributes(request, captor.getValue());
        verify(patientRepository).save(captor.getValue());
        assertThat(captor.getValue().getRoles()).isEqualTo(roles);
        assertThat(result).isSameAs(persisted);
    }
}
