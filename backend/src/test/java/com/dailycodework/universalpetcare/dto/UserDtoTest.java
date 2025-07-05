package com.dailycodework.universalpetcare.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testUserDtoFields() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setGender("Male");
        userDto.setPhoneNumber("1234567890");
        userDto.setEmail("john@example.com");
        userDto.setUserType("USER");
        userDto.setEnabled(true);
        userDto.setSpecialization("Vet");
        userDto.setCreatedAt(LocalDate.of(2024, 1, 1));
        userDto.setAppointments(List.of());
        userDto.setReviews(List.of());
        userDto.setPhotoId(100L);
        userDto.setPhoto(new byte[]{1, 2, 3});
        userDto.setAverageRating(4.5);
        userDto.setRoles(Set.of("ADMIN", "USER"));
        userDto.setTotalReviewers(10L);

        assertEquals(1L, userDto.getId());
        assertEquals("John", userDto.getFirstName());
        assertEquals("Doe", userDto.getLastName());
        assertEquals("Male", userDto.getGender());
        assertEquals("1234567890", userDto.getPhoneNumber());
        assertEquals("john@example.com", userDto.getEmail());
        assertEquals("USER", userDto.getUserType());
        assertTrue(userDto.isEnabled());
        assertEquals("Vet", userDto.getSpecialization());
        assertEquals(LocalDate.of(2024, 1, 1), userDto.getCreatedAt());
        assertNotNull(userDto.getAppointments());
        assertNotNull(userDto.getReviews());
        assertEquals(100L, userDto.getPhotoId());
        assertArrayEquals(new byte[]{1, 2, 3}, userDto.getPhoto());
        assertEquals(4.5, userDto.getAverageRating());
        assertTrue(userDto.getRoles().contains("ADMIN"));
        assertEquals(10L, userDto.getTotalReviewers());
    }
}