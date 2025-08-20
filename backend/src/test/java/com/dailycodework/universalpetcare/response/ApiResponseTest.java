package com.dailycodework.universalpetcare.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testApiResponseFields() {
        String message = "Success";
        Object data = new Object();

        ApiResponse response = new ApiResponse(message, data);

        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
    }
}