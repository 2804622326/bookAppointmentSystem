package com.dailycodework.universalpetcare.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    @Test
    void testRegistrationRequestFields() {
        RegistrationRequest request = new RegistrationRequest();
        request.setId(1L);
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setGender("Female");
        request.setPhoneNumber("1234567890");
        request.setEmail("jane@example.com");
        request.setPassword("securePassword");
        request.setUserType("USER");
        request.setEnabled(true);
        request.setSpecialization("Dentistry");

        assertEquals(1L, request.getId());
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("Female", request.getGender());
        assertEquals("1234567890", request.getPhoneNumber());
        assertEquals("jane@example.com", request.getEmail());
        assertEquals("securePassword", request.getPassword());
        assertEquals("USER", request.getUserType());
        assertTrue(request.isEnabled());
        assertEquals("Dentistry", request.getSpecialization());
    }
}