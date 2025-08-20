package com.dailycodework.universalpetcare.dto;

import com.dailycodework.universalpetcare.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

class EntityConverterPetDtoTest {

    private EntityConverter<Pet, PetDto> converter;

    @BeforeEach
    void setUp() {
        converter = new EntityConverter<>(new ModelMapper());
    }

    @Test
    void testMapPetToPetDto() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setType("Dog");
        pet.setColor("Brown");
        pet.setBreed("Labrador");
        pet.setAge(3);

        PetDto dto = converter.mapEntityToDto(pet, PetDto.class);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Buddy", dto.getName());
        assertEquals("Dog", dto.getType());
        assertEquals("Brown", dto.getColor());
        assertEquals("Labrador", dto.getBreed());
        assertEquals(3, dto.getAge());
    }

    @Test
    void testMapPetDtoToPet() {
        PetDto dto = new PetDto();
        dto.setId(2L);
        dto.setName("Whiskers");
        dto.setType("Cat");
        dto.setColor("Black");
        dto.setBreed("Persian");
        dto.setAge(2);

        Pet pet = converter.mapDtoToEntity(dto, Pet.class);

        assertNotNull(pet);
        assertEquals(2L, pet.getId());
        assertEquals("Whiskers", pet.getName());
        assertEquals("Cat", pet.getType());
        assertEquals("Black", pet.getColor());
        assertEquals("Persian", pet.getBreed());
        assertEquals(2, pet.getAge());
    }
}
