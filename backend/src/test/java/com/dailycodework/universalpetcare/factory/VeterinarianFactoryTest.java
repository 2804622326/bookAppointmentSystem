package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
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
class VeterinarianFactoryTest {

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Mock
    private UserAttributesMapper userAttributesMapper;

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private VeterinarianFactory veterinarianFactory;

    @Test
    void createVeterinarian_whenCalled_setsRolesMapsAttributesAndPersists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setSpecialization("Surgery");
        Veterinarian persisted = new Veterinarian();
        Set<Role> roles = Set.of(new Role());

        when(roleService.setUserRole("VET")).thenReturn(roles);
        ArgumentCaptor<Veterinarian> captor = ArgumentCaptor.forClass(Veterinarian.class);
        when(veterinarianRepository.save(captor.capture())).thenReturn(persisted);

        Veterinarian result = veterinarianFactory.createVeterinarian(request);

        verify(roleService).setUserRole("VET");
        verify(userAttributesMapper).setCommonAttributes(request, captor.getValue());
        verify(veterinarianRepository).save(captor.getValue());
        assertThat(captor.getValue().getRoles()).isEqualTo(roles);
        assertThat(captor.getValue().getSpecialization()).isEqualTo("Surgery");
        assertThat(result).isSameAs(persisted);
    }
}
