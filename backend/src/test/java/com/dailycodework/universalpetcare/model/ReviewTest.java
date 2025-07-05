package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testReviewFields() {
        User vet = new User();
        User patient = new User();

        Review review = new Review();
        review.setId(1L);
        review.setFeedback("Great service!");
        review.setStars(5);
        review.setVeterinarian(vet);
        review.setPatient(patient);

        assertEquals(1L, review.getId());
        assertEquals("Great service!", review.getFeedback());
        assertEquals(5, review.getStars());
        assertEquals(vet, review.getVeterinarian());
        assertEquals(patient, review.getPatient());
    }

    @Test
    void testAllArgsConstructor() {
        User vet = new User();
        User patient = new User();
        Review review = new Review(2L, "Very good", 4, vet, patient);

        assertEquals(2L, review.getId());
        assertEquals("Very good", review.getFeedback());
        assertEquals(4, review.getStars());
        assertEquals(vet, review.getVeterinarian());
        assertEquals(patient, review.getPatient());
    }

    @Test
    void testRemoveRelationship() {
        Review review = new Review();
        User vet = new User();
        User patient = new User();
        vet.setReviews(new ArrayList<>());
        patient.setReviews(new ArrayList<>());

        vet.getReviews().add(review);
        patient.getReviews().add(review);

        review.setVeterinarian(vet);
        review.setPatient(patient);

        review.removeRelationShip();

        assertFalse(vet.getReviews().contains(review));
        assertFalse(patient.getReviews().contains(review));
    }
}