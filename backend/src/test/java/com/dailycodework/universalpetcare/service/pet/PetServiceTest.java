package com.dailycodework.universalpetcare.service.pet;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.PetRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private PetService petService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSavePetsForAppointment_noAppointmentId() {
        List<Pet> pets = List.of(new Pet(), new Pet());
        when(petRepository.saveAll(pets)).thenReturn(pets);

        List<Pet> result = petService.savePetsForAppointment(pets);

        assertEquals(2, result.size());
        verify(petRepository).saveAll(pets);
    }

    @Test
    void testSavePetsForAppointment_withAppointmentId_success() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        List<Pet> pets = List.of(new Pet(), new Pet());

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Pet> result = petService.savePetsForAppointment(appointmentId, pets);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getAppointment() == appointment));
        verify(petRepository, times(2)).save(any(Pet.class));
    }

    @Test
    void testSavePetsForAppointment_withAppointmentId_notFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () ->
                petService.savePetsForAppointment(99L, List.of(new Pet())));
    }

    @Test
    void testUpdatePet_success() {
        Long petId = 1L;
        Pet existing = new Pet();
        existing.setName("Old");

        Pet newPet = new Pet();
        newPet.setName("New");
        newPet.setAge(2);
        newPet.setColor("Brown");
        newPet.setType("Dog");
        newPet.setBreed("Labrador");

        when(petRepository.findById(petId)).thenReturn(Optional.of(existing));
        when(petRepository.save(any())).thenReturn(existing);

        Pet updated = petService.updatePet(newPet, petId);

        assertEquals("New", updated.getName());
        assertEquals("Brown", updated.getColor());
    }

    @Test
    void testDeletePet_success() {
        Pet pet = new Pet();
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        petService.deletePet(1L);

        verify(petRepository).delete(pet);
    }

    @Test
    void testDeletePet_notFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.deletePet(1L));
    }

    @Test
    void testGetPetById_success() {
        Pet pet = new Pet();
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Pet result = petService.getPetById(1L);

        assertEquals(pet, result);
    }

    @Test
    void testGetPetById_notFound() {
        when(petRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> petService.getPetById(2L));
        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, ex.getMessage());
    }

    @Test
    void testGetPetTypes() {
        List<String> types = Arrays.asList("Dog", "Cat");
        when(petRepository.getDistinctPetTypes()).thenReturn(types);

        List<String> result = petService.getPetTypes();

        assertEquals(types, result);
    }

    @Test
    void testGetPetColors() {
        List<String> colors = Arrays.asList("Black", "White");
        when(petRepository.getDistinctPetColors()).thenReturn(colors);

        List<String> result = petService.getPetColors();

        assertEquals(colors, result);
    }

    @Test
    void testGetPetBreeds() {
        List<String> breeds = List.of("Labrador", "Poodle");
        when(petRepository.getDistinctPetBreedsByPetType("Dog")).thenReturn(breeds);

        List<String> result = petService.getPetBreeds("Dog");

        assertEquals(breeds, result);
    }
}
