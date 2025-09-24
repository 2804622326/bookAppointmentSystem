package com.dailycodework.universalpetcare.dto;

import com.dailycodework.universalpetcare.config.ApplicationConfig;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntityConverterReviewDtoTest {

    private EntityConverter<Review, ReviewDto> entityConverter;

    @BeforeEach
    void setUp() {
        entityConverter = new EntityConverter<>(new ApplicationConfig().modelMapper());
    }

    @Test
    void mapReviewToDto_populatesFullNamesAndIds() {
        User patient = new User();
        patient.setId(10L);
        patient.setFirstName("John");
        patient.setLastName("Doe");

        User veterinarian = new User();
        veterinarian.setId(20L);
        veterinarian.setFirstName("Dr.");
        veterinarian.setLastName("Smith");

        Review review = new Review();
        review.setId(1L);
        review.setStars(5);
        review.setFeedback("Excellent service!");
        review.setPatient(patient);
        review.setVeterinarian(veterinarian);

        ReviewDto dto = entityConverter.mapEntityToDto(review, ReviewDto.class);

        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getStars());
        assertEquals("Excellent service!", dto.getFeedback());
        assertEquals(10L, dto.getPatientId());
        assertEquals(20L, dto.getVeterinarianId());
        assertEquals("John Doe", dto.getPatientName());
        assertEquals("Dr. Smith", dto.getVeterinarianName());
    }

    @Test
    void mapReviewToDto_omitsExtraSpacesWhenPartMissing() {
        User patient = new User();
        patient.setFirstName("Jane");

        User veterinarian = new User();
        veterinarian.setLastName("Solo");

        Review review = new Review();
        review.setPatient(patient);
        review.setVeterinarian(veterinarian);

        ReviewDto dto = entityConverter.mapEntityToDto(review, ReviewDto.class);

        assertEquals("Jane", dto.getPatientName());
        assertEquals("Solo", dto.getVeterinarianName());
    }

    @Test
    void mapReviewToDto_returnsNullNameWhenBothPartsMissing() {
        Review review = new Review();
        review.setPatient(new User());
        review.setVeterinarian(new User());

        ReviewDto dto = entityConverter.mapEntityToDto(review, ReviewDto.class);

        assertNull(dto.getPatientName());
        assertNull(dto.getVeterinarianName());
    }
}
