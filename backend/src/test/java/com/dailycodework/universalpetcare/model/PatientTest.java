package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void testPatientFields() {
        Patient patient = new Patient();
        patient.setId(1L);

        assertEquals(1L, patient.getId());
    }

    @Test
    void testAllArgsConstructor() {
        Patient patient = new Patient(2L);
        assertEquals(2L, patient.getId());
    }
}