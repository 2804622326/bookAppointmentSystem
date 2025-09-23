package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.RoleRepository;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VeterinarianControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void cleanDatabase() {
        veterinarianRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getAllVeterinarians_returnsPersistedVeterinarians() throws Exception {
        Role vetRole = roleRepository.save(new Role("ROLE_VET"));

        Veterinarian firstVet = new Veterinarian();
        firstVet.setFirstName("Alex");
        firstVet.setLastName("Jones");
        firstVet.setGender("Male");
        firstVet.setPhoneNumber("1111111111");
        firstVet.setEmail("vet.one@example.com");
        firstVet.setPassword("password1");
        firstVet.setUserType("VET");
        firstVet.setEnabled(true);
        firstVet.setSpecialization("Dermatology");
        firstVet.setRoles(Set.of(vetRole));
        veterinarianRepository.save(firstVet);

        Veterinarian secondVet = new Veterinarian();
        secondVet.setFirstName("Blair");
        secondVet.setLastName("Smith");
        secondVet.setGender("Female");
        secondVet.setPhoneNumber("2222222222");
        secondVet.setEmail("vet.two@example.com");
        secondVet.setPassword("password2");
        secondVet.setUserType("VET");
        secondVet.setEnabled(true);
        secondVet.setSpecialization("Surgery");
        secondVet.setRoles(Set.of(vetRole));
        veterinarianRepository.save(secondVet);

        mockMvc.perform(get("/api/v1/veterinarians/get-all-veterinarians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_FOUND))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].email", containsInAnyOrder("vet.one@example.com", "vet.two@example.com")))
                .andExpect(jsonPath("$.data[*].specialization", containsInAnyOrder("Dermatology", "Surgery")));
    }
}
