package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Veterinarian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VeterinarianRepositoryTest {

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    private Veterinarian testVet;

    @BeforeEach
    void setUp() {
        testVet = new Veterinarian();
        testVet.setFirstName("John");
        testVet.setLastName("Doe");
        testVet.setUserType("VET");
        testVet.setSpecialization("Surgery");
        veterinarianRepository.save(testVet);
    }

    @Test
    void testFindBySpecialization() {
        List<Veterinarian> result = veterinarianRepository.findBySpecialization("Surgery");
        assertFalse(result.isEmpty());
        assertEquals("Surgery", result.get(0).getSpecialization());
    }

    @Test
    void testExistsBySpecialization() {
        boolean exists = veterinarianRepository.existsBySpecialization("Surgery");
        assertTrue(exists);
    }

    @Test
    void testGetSpecializations() {
        List<String> specializations = veterinarianRepository.getSpecializations();
        assertTrue(specializations.contains("Surgery"));
    }

    @Test
    void testCountVetsBySpecialization() {
        List<Object[]> result = veterinarianRepository.countVetsBySpecialization();
        assertFalse(result.isEmpty());

        boolean found = result.stream().anyMatch(row ->
                row[0].equals("Surgery") && ((Long) row[1]) >= 1L
        );
        assertTrue(found);
    }
}
