package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.service.role.IRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {
        Role role1 = new Role();
        Role role2 = new Role();
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleService.getAllRoles()).thenReturn(roles);

        List<Role> result = roleController.getAllRoles();

        assertEquals(2, result.size());
        assertEquals(roles, result);
    }

    @Test
    void testGetRoleById() {
        Role role = new Role();
        role.setId(1L);

        when(roleService.getRoleById(1L)).thenReturn(role);

        Role result = roleController.getRoleById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void testGetRoleByName() {
        Role role = new Role();
        role.setName("ADMIN");

        when(roleService.getRoleByName("ADMIN")).thenReturn(role);

        Role result = roleController.getRoleByName("ADMIN");

        assertEquals("ADMIN", result.getName());
    }
}
