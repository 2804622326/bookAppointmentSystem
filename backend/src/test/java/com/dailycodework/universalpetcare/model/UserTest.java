package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserFields() {
        User user = new User();
        Photo photo = new Photo();
        Role role = new Role("USER");
        VerificationToken token = new VerificationToken();

        user.setId(1L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setGender("Female");
        user.setPhoneNumber("1234567890");
        user.setEmail("alice@example.com");
        user.setPassword("password123");
        user.setUserType("PATIENT");
        user.setEnabled(true);
        user.setCreatedAt(LocalDate.of(2025, 6, 1));
        user.setSpecialization("Dermatology");
        user.setAppointments(new ArrayList<>());
        user.setReviews(new ArrayList<>());
        user.setPhoto(photo);
        user.setRoles(Set.of(role));
        user.setVerificationTokens(List.of(token));

        assertEquals(1L, user.getId());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("Female", user.getGender());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("PATIENT", user.getUserType());
        assertTrue(user.isEnabled());
        assertEquals(LocalDate.of(2025, 6, 1), user.getCreatedAt());
        assertEquals("Dermatology", user.getSpecialization());
        assertNotNull(user.getAppointments());
        assertNotNull(user.getReviews());
        assertEquals(photo, user.getPhoto());
        assertTrue(user.getRoles().contains(role));
        assertEquals(1, user.getVerificationTokens().size());
    }

    @Test
    void testRemoveUserPhoto() {
        User user = new User();
        Photo photo = new Photo();
        user.setPhoto(photo);

        user.removeUserPhoto();

        assertNull(user.getPhoto());
    }
}