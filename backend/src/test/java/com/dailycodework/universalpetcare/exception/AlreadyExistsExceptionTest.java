package com.dailycodework.universalpetcare.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlreadyExistsExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "User already exists";
        AlreadyExistsException exception = new AlreadyExistsException(message);

        assertEquals(message, exception.getMessage());
    }
}