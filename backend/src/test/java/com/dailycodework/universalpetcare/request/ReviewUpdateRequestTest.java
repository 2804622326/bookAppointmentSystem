package com.dailycodework.universalpetcare.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewUpdateRequestTest {

    @Test
    void testReviewUpdateRequestFields() {
        ReviewUpdateRequest request = new ReviewUpdateRequest();
        request.setStars(5);
        request.setFeedback("Excellent service!");

        assertEquals(5, request.getStars());
        assertEquals("Excellent service!", request.getFeedback());
    }
}