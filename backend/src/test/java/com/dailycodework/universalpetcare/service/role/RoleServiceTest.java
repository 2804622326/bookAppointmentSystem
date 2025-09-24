package com.dailycodework.universalpetcare.service.role;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.repository.RoleRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");
    }

    @Test
    void getAllRoles_whenRepositoryReturnsList_returnsSameList() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<Role> result = roleService.getAllRoles();

        assertThat(result).containsExactly(role);
        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById_whenRoleExists_returnsRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(1L);

        assertThat(result).isSameAs(role);
        verify(roleRepository).findById(1L);
    }

    @Test
    void getRoleById_whenRoleMissing_returnsNull() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Role result = roleService.getRoleById(1L);

        assertThat(result).isNull();
    }

    @Test
    void getRoleByName_whenRoleExists_returnsRole() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        Role result = roleService.getRoleByName("ROLE_ADMIN");

        assertThat(result).isSameAs(role);
        verify(roleRepository).findByName("ROLE_ADMIN");
    }

    @Test
    void getRoleByName_whenRoleMissing_returnsNull() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        Role result = roleService.getRoleByName("ROLE_ADMIN");

        assertThat(result).isNull();
    }

    @Test
    void saveRole_always_delegatesToRepository() {
        roleService.saveRole(role);

        verify(roleRepository).save(role);
    }

    @Test
    void setUserRole_whenRoleExists_returnsSetWithRole() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        Set<Role> roles = roleService.setUserRole("admin");

        assertThat(roles).containsExactly(role);
    }

    @Test
    void setUserRole_whenRoleMissing_throwsResourceNotFoundException() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.setUserRole("ADMIN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(FeedBackMessage.ROLE_NOT_FOUND);
    }

    @Test
    void setUserRole_whenTypeBlank_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> roleService.setUserRole("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blank");
    }

    @Test
    void setUserRole_whenTypeNull_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> roleService.setUserRole(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }
}
