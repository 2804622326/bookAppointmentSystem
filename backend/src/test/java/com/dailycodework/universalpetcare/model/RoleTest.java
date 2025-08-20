package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testRoleFields() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setUsers(new HashSet<>());

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getName());
        assertNotNull(role.getUsers());
    }

    @Test
    void testConstructorWithName() {
        Role role = new Role("USER");

        assertEquals("USER", role.getName());
    }

    @Test
    void testGetNameWhenNull() {
        Role role = new Role();
        role.setName(null);

        assertEquals("", role.getName());
    }

    @Test
    void testToString() {
        Role role = new Role("MANAGER");

        assertEquals("MANAGER", role.toString());
    }
}