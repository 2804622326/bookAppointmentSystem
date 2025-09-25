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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    private Role adminRole;
    private Role patientRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("ROLE_USER");

        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");

        patientRole = new Role();
        patientRole.setId(3L);
        patientRole.setName("ROLE_PATIENT");
    }

    @Test
    void testGetAllRoles_Success() {
        // Given
        List<Role> expectedRoles = Arrays.asList(testRole, adminRole, patientRole);
        when(roleRepository.findAll()).thenReturn(expectedRoles);

        // When
        List<Role> actualRoles = roleService.getAllRoles();

        // Then
        assertEquals(expectedRoles.size(), actualRoles.size());
        assertEquals(expectedRoles, actualRoles);
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetAllRoles_EmptyList() {
        // Given
        List<Role> emptyList = new ArrayList<>();
        when(roleRepository.findAll()).thenReturn(emptyList);

        // When
        List<Role> actualRoles = roleService.getAllRoles();

        // Then
        assertTrue(actualRoles.isEmpty());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetRoleById_Success() {
        // Given
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(testRole));

        // When
        Role actualRole = roleService.getRoleById(roleId);

        // Then
        assertEquals(testRole, actualRole);
        assertEquals(testRole.getId(), actualRole.getId());
        assertEquals(testRole.getName(), actualRole.getName());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testGetRoleById_NotFound() {
        // Given
        Long nonExistentId = 999L;
        when(roleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Role actualRole = roleService.getRoleById(nonExistentId);

        // Then
        assertNull(actualRole);
        verify(roleRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetRoleByName_Success() {
        // Given
        String roleName = "ROLE_USER";
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(testRole));

        // When
        Role actualRole = roleService.getRoleByName(roleName);

        // Then
        assertEquals(testRole, actualRole);
        assertEquals(testRole.getName(), actualRole.getName());
        verify(roleRepository, times(1)).findByName(roleName);
    }

    @Test
    void testGetRoleByName_NotFound() {
        // Given
        String nonExistentRoleName = "ROLE_NONEXISTENT";
        when(roleRepository.findByName(nonExistentRoleName)).thenReturn(Optional.empty());

        // When
        Role actualRole = roleService.getRoleByName(nonExistentRoleName);

        // Then
        assertNull(actualRole);
        verify(roleRepository, times(1)).findByName(nonExistentRoleName);
    }

    @Test
    void testSaveRole_Success() {
        // Given
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // When
        roleService.saveRole(testRole);

        // Then
        verify(roleRepository, times(1)).save(testRole);
    }

    @Test
    void testSaveRole_NewRole() {
        // Given
        Role newRole = new Role();
        newRole.setName("ROLE_NEW");
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);

        // When
        roleService.saveRole(newRole);

        // Then
        verify(roleRepository, times(1)).save(newRole);
    }

    @Test
    void testSetUserRole_Success_Patient() {
        // Given
        String userType = "PATIENT";
        String expectedRoleName = "ROLE_PATIENT";
        when(roleRepository.findByName(expectedRoleName)).thenReturn(Optional.of(patientRole));

        // When
        Set<Role> userRoles = roleService.setUserRole(userType);

        // Then
        assertEquals(1, userRoles.size());
        assertTrue(userRoles.contains(patientRole));
        verify(roleRepository, times(1)).findByName(expectedRoleName);
    }

    @Test
    void testSetUserRole_Success_Admin() {
        // Given
        String userType = "ADMIN";
        String expectedRoleName = "ROLE_ADMIN";
        when(roleRepository.findByName(expectedRoleName)).thenReturn(Optional.of(adminRole));

        // When
        Set<Role> userRoles = roleService.setUserRole(userType);

        // Then
        assertEquals(1, userRoles.size());
        assertTrue(userRoles.contains(adminRole));
        verify(roleRepository, times(1)).findByName(expectedRoleName);
    }

    @Test
    void testSetUserRole_RoleNotFound() {
        // Given
        String userType = "INVALID";
        String expectedRoleName = "ROLE_INVALID";
        when(roleRepository.findByName(expectedRoleName)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> roleService.setUserRole(userType)
        );

        assertEquals(FeedBackMessage.ROLE_NOT_FOUND, exception.getMessage());
        verify(roleRepository, times(1)).findByName(expectedRoleName);
    }

    @Test
    void testSetUserRole_EmptyUserType() {
        // Given
        String userType = "";
        String expectedRoleName = "ROLE_";
        when(roleRepository.findByName(expectedRoleName)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> roleService.setUserRole(userType)
        );

        assertEquals(FeedBackMessage.ROLE_NOT_FOUND, exception.getMessage());
        verify(roleRepository, times(1)).findByName(expectedRoleName);
    }

    @Test
    void testSetUserRole_NullUserType() {
        // Given
        String userType = null;
        String expectedRoleName = "ROLE_null";
        when(roleRepository.findByName(expectedRoleName)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> roleService.setUserRole(userType)
        );

        assertEquals(FeedBackMessage.ROLE_NOT_FOUND, exception.getMessage());
        verify(roleRepository, times(1)).findByName(expectedRoleName);
    }
}