package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAttributesMapperTest {

    private UserAttributesMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserAttributesMapper();
    }

    @Test
    void testSetCommonAttributes_success() {
        // Given
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setGender("Male");
        request.setPhoneNumber("1234567890");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setEnabled(true);
        request.setUserType("PATIENT");

        User user = new User();

        // When
        mapper.setCommonAttributes(request, user);

        // Then
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Male", user.getGender());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertTrue(user.isEnabled());
        assertEquals("PATIENT", user.getUserType());
    }

    @Test
    void testSetCommonAttributes_withNullValues() {
        // Given
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName(null);
        request.setLastName(null);
        request.setGender(null);
        request.setPhoneNumber(null);
        request.setEmail(null);
        request.setPassword(null);
        request.setEnabled(false);
        request.setUserType(null);

        User user = new User();

        // When
        mapper.setCommonAttributes(request, user);

        // Then
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getGender());
        assertNull(user.getPhoneNumber());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertFalse(user.isEnabled());
        assertNull(user.getUserType());
    }
}