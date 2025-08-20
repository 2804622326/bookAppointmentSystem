package com.dailycodework.universalpetcare.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeterinarianDtoTest {

    @Test
    void testVeterinarianDtoFields() {
        VeterinarianDto vetDto = new VeterinarianDto();
        vetDto.setVeterinarianId(1L);
        vetDto.setFirstName("Alice");
        vetDto.setLastName("Smith");
        vetDto.setEmail("alice@example.com");
        vetDto.setGender("Female");
        vetDto.setPhoneNumber("9876543210");
        vetDto.setSpecialization("Dermatology");

        assertEquals(1L, vetDto.getVeterinarianId());
        assertEquals("Alice", vetDto.getFirstName());
        assertEquals("Smith", vetDto.getLastName());
        assertEquals("alice@example.com", vetDto.getEmail());
        assertEquals("Female", vetDto.getGender());
        assertEquals("9876543210", vetDto.getPhoneNumber());
        assertEquals("Dermatology", vetDto.getSpecialization());
    }
}