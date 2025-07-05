package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private DummyRoleCaller dummyRoleCaller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByName_RoleExists() {
        String roleName = "ADMIN";
        Role mockRole = new Role();
        mockRole.setName(roleName);

        // 模拟行为
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(mockRole));

        Optional<Role> result = dummyRoleCaller.findRoleByName(roleName);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());

        verify(roleRepository).findByName(roleName);
    }

    @Test
    void testFindByName_RoleNotFound() {
        String roleName = "UNKNOWN";

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Optional<Role> result = dummyRoleCaller.findRoleByName(roleName);

        assertFalse(result.isPresent());

        verify(roleRepository).findByName(roleName);
    }

    // ========= 帮助类，用于调用 RoleRepository =========
    static class DummyRoleCaller {
        @InjectMocks
        private RoleRepository roleRepository;

        public Optional<Role> findRoleByName(String name) {
            return roleRepository.findByName(name);
        }
    }
}