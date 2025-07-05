package com.dailycodework.universalpetcare.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateRequestTest {

    @Test
    void testUserUpdateRequestFields() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Tom");
        request.setLastName("Brown");
        request.setGender("Male");
        request.setPhoneNumber("1234567890");
        request.setSpecialization("Surgery");

        assertEquals("Tom", request.getFirstName());
        assertEquals("Brown", request.getLastName());
        assertEquals("Male", request.getGender());
        assertEquals("1234567890", request.getPhoneNumber());
        assertEquals("Surgery", request.getSpecialization());
    }
}