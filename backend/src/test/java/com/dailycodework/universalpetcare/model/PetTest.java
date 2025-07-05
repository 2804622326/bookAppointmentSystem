package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {

    @Test
    void testPetFields() {
        Pet pet = new Pet();
        Appointment appointment = new Appointment();

        pet.setId(1L);
        pet.setName("Buddy");
        pet.setType("Dog");
        pet.setColor("Brown");
        pet.setBreed("Labrador");
        pet.setAge(5);
        pet.setAppointment(appointment);

        assertEquals(1L, pet.getId());
        assertEquals("Buddy", pet.getName());
        assertEquals("Dog", pet.getType());
        assertEquals("Brown", pet.getColor());
        assertEquals("Labrador", pet.getBreed());
        assertEquals(5, pet.getAge());
        assertEquals(appointment, pet.getAppointment());
    }

    @Test
    void testAllArgsConstructor() {
        Appointment appointment = new Appointment();
        Pet pet = new Pet(2L, "Max", "Cat", "White", "Persian", 3, appointment);

        assertEquals(2L, pet.getId());
        assertEquals("Max", pet.getName());
        assertEquals("Cat", pet.getType());
        assertEquals("White", pet.getColor());
        assertEquals("Persian", pet.getBreed());
        assertEquals(3, pet.getAge());
        assertEquals(appointment, pet.getAppointment());
    }
}