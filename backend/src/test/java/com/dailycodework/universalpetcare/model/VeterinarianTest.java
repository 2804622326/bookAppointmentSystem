package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeterinarianTest {

    @Test
    void testVeterinarianFields() {
        Veterinarian vet = new Veterinarian();
        vet.setId(1L);
        vet.setSpecialization("Surgery");

        assertEquals(1L, vet.getId());
        assertEquals("Surgery", vet.getSpecialization());
    }

    @Test
    void testAllArgsConstructor() {
        Veterinarian vet = new Veterinarian(2L, "Dentistry");

        assertEquals(2L, vet.getId());
        assertEquals("Dentistry", vet.getSpecialization());
    }
}