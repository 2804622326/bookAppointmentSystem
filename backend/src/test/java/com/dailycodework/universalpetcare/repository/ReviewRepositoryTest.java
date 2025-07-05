package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Review;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewRepositoryTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    public void testFindAllByUserIdWithPageable() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review());
        Page<Review> expectedPage = new PageImpl<>(reviews, pageable, reviews.size());

        when(reviewRepository.findAllByUserId(userId, pageable)).thenReturn(expectedPage);

        Page<Review> result = reviewRepository.findAllByUserId(userId, pageable);

        assertNotNull(result);
        assertEquals(expectedPage.getContent(), result.getContent());
    }

    @Test
    public void testFindAllByUserIdWithoutPageable() {
        Long userId = 1L;
        List<Review> expectedReviews = new ArrayList<>();
        expectedReviews.add(new Review());

        when(reviewRepository.findAllByUserId(userId)).thenReturn(expectedReviews);

        List<Review> result = reviewRepository.findAllByUserId(userId);

        assertNotNull(result);
        assertEquals(expectedReviews, result);
    }

    @Test
    public void testFindByVeterinarianId() {
        Long veterinarianId = 1L;
        List<Review> expectedReviews = new ArrayList<>();
        expectedReviews.add(new Review());

        when(reviewRepository.findByVeterinarianId(veterinarianId)).thenReturn(expectedReviews);

        List<Review> result = reviewRepository.findByVeterinarianId(veterinarianId);

        assertNotNull(result);
        assertEquals(expectedReviews, result);
    }

    @Test
    public void testFindByVeterinarianIdAndPatientId() {
        Long veterinarianId = 1L;
        Long reviewerId = 2L;
        Review review = new Review();
        Optional<Review> expectedOptional = Optional.of(review);

        when(reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId)).thenReturn(expectedOptional);

        Optional<Review> result = reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId);

        assertTrue(result.isPresent());
        assertEquals(expectedOptional.get(), result.get());
    }

    @Test
    public void testCountByVeterinarianId() {
        Long id = 1L;
        Long expectedCount = 5L;

        when(reviewRepository.countByVeterinarianId(id)).thenReturn(expectedCount);

        Long result = reviewRepository.countByVeterinarianId(id);

        assertNotNull(result);
        assertEquals(expectedCount, result);
    }
}