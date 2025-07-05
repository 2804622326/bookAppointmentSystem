package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Pet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PetRepositoryTest {

    @Mock
    private PetRepository petRepository;

    @Test
    public void testGetDistinctPetTypes() {
        List<String> expectedTypes = Arrays.asList("Dog", "Cat");

        when(petRepository.getDistinctPetTypes()).thenReturn(expectedTypes);

        List<String> actualTypes = petRepository.getDistinctPetTypes();

        assertEquals(expectedTypes, actualTypes);
    }

    @Test
    public void testGetDistinctPetColors() {
        List<String> expectedColors = Arrays.asList("Black", "White");

        when(petRepository.getDistinctPetColors()).thenReturn(expectedColors);

        List<String> actualColors = petRepository.getDistinctPetColors();

        assertEquals(expectedColors, actualColors);
    }

    @Test
    public void testGetDistinctPetBreedsByPetType() {
        String petType = "Dog";
        List<String> expectedBreeds = Arrays.asList("Labrador", "German Shepherd");

        when(petRepository.getDistinctPetBreedsByPetType(petType)).thenReturn(expectedBreeds);

        List<String> actualBreeds = petRepository.getDistinctPetBreedsByPetType(petType);

        assertEquals(expectedBreeds, actualBreeds);
    }
}