package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.model.Patient;
import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.repository.PatientRepository;
import com.dailycodework.universalpetcare.repository.RoleRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void cleanDatabase() {
        patientRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getAllPatients_returnsPersistedPatients() throws Exception {
        Role patientRole = roleRepository.save(new Role("ROLE_PATIENT"));

        Patient patient = new Patient();
        patient.setFirstName("Test");
        patient.setLastName("Patient");
        patient.setGender("Non-binary");
        patient.setPhoneNumber("1234567890");
        patient.setEmail("test.patient@example.com");
        patient.setPassword("password");
        patient.setUserType("PATIENT");
        patient.setEnabled(true);
        patient.setRoles(Set.of(patientRole));
        patientRepository.save(patient);

        mockMvc.perform(get("/api/v1/patients/get-all-patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_FOUND))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].email").value("test.patient@example.com"))
                .andExpect(jsonPath("$.data[0].firstName").value("Test"));
    }
}
