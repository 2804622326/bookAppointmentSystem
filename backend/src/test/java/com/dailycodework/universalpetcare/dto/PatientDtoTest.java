package com.dailycodework.universalpetcare.dto;

import com.dailycodework.universalpetcare.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityConverterPatientDtoTest {
    private EntityConverter<User, PatientDto> entityConverter;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(User.class, PatientDto.class)
                .addMapping(User::getId, PatientDto::setPatientId);
        entityConverter = new EntityConverter<>(modelMapper);
    }

    @Test
    void testMapUserToPatientDto() {
        User user = new User();
        user.setId(123L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice@example.com");
        user.setGender("FEMALE");
        user.setPhoneNumber("1234567890");

        PatientDto dto = entityConverter.mapEntityToDto(user, PatientDto.class);

        assertEquals(123L, dto.getPatientId());
        assertEquals("Alice", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("FEMALE", dto.getGender());
        assertEquals("1234567890", dto.getPhoneNumber());
    }

}