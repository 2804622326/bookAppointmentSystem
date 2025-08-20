package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.pet.IPetService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetControllerTest {

    @Mock
    private IPetService petService;

    @InjectMocks
    private PetController petController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSavePets_success() {
        List<Pet> pets = Arrays.asList(new Pet(), new Pet());
        when(petService.savePetsForAppointment(1L, pets)).thenReturn(pets);

        ResponseEntity<ApiResponse> response = petController.savePets(1L, pets);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PET_ADDED_SUCCESS, response.getBody().getMessage());
        assertEquals(pets, response.getBody().getData());
    }

    @Test
    void testSavePets_error() {
        List<Pet> pets = Collections.emptyList();
        when(petService.savePetsForAppointment(1L, pets)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<ApiResponse> response = petController.savePets(1L, pets);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("DB error", response.getBody().getMessage());
    }

    @Test
    void testGetPetById_success() {
        Pet pet = new Pet();
        when(petService.getPetById(1L)).thenReturn(pet);

        ResponseEntity<ApiResponse> response = petController.getPetById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PET_FOUND, response.getBody().getMessage());
        assertEquals(pet, response.getBody().getData());
    }

    @Test
    void testGetPetById_notFound() {
        when(petService.getPetById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        ResponseEntity<ApiResponse> response = petController.getPetById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void testDeletePetById_success() {
        ResponseEntity<ApiResponse> response = petController.deletePetById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PET_DELETE_SUCCESS, response.getBody().getMessage());
        verify(petService).deletePet(1L);
    }

    @Test
    void testUpdatePet_success() {
        Pet pet = new Pet();
        when(petService.updatePet(pet, 1L)).thenReturn(pet);

        ResponseEntity<ApiResponse> response = petController.updatePet(1L, pet);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(pet, response.getBody().getData());
        assertEquals(FeedBackMessage.PET_UPDATE_SUCCESS, response.getBody().getMessage());
    }

    @Test
    void testGetAllPetTypes() {
        List<String> types = Arrays.asList("Dog", "Cat");
        when(petService.getPetTypes()).thenReturn(types);

        ResponseEntity<ApiResponse> response = petController.getAllPetTypes();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(types, response.getBody().getData());
    }

    @Test
    void testGetAllPetColors() {
        List<String> colors = Arrays.asList("Black", "White");
        when(petService.getPetColors()).thenReturn(colors);

        ResponseEntity<ApiResponse> response = petController.getAllPetColors();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(colors, response.getBody().getData());
    }

    @Test
    void testGetAllPetBreeds() {
        List<String> breeds = Arrays.asList("Labrador", "Bulldog");
        when(petService.getPetBreeds("Dog")).thenReturn(breeds);

        ResponseEntity<ApiResponse> response = petController.getAllPetBreeds("Dog");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(breeds, response.getBody().getData());
    }
}
