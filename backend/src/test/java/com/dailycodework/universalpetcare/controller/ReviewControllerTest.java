package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.ReviewDto;
import com.dailycodework.universalpetcare.exception.AlreadyExistsException;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.request.ReviewUpdateRequest;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.review.IReviewService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @Mock
    private IReviewService reviewService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveReview_success() {
        Review review = new Review();
        review.setId(1L);

        when(reviewService.saveReview(review, 10L, 20L)).thenReturn(review);

        ResponseEntity<ApiResponse> response = reviewController.saveReview(10L, 20L, review);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.REVIEW_SUBMIT_SUCCESS, response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData());
    }

    @Test
    void testSaveReview_conflict() {
        Review review = new Review();
        when(reviewService.saveReview(review, 10L, 20L)).thenThrow(new AlreadyExistsException("Duplicate"));

        ResponseEntity<ApiResponse> response = reviewController.saveReview(10L, 20L, review);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Duplicate", response.getBody().getMessage());
    }

    @Test
    void testUpdateReview_success() {
        ReviewUpdateRequest request = new ReviewUpdateRequest();
        Review updated = new Review();
        updated.setId(100L);

        when(reviewService.updateReview(1L, request)).thenReturn(updated);

        ResponseEntity<ApiResponse> response = reviewController.updateReview(request, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.REVIEW_UPDATE_SUCCESS, response.getBody().getMessage());
        assertEquals(100L, response.getBody().getData());
    }

    @Test
    void testUpdateReview_notFound() {
        ReviewUpdateRequest request = new ReviewUpdateRequest();
        when(reviewService.updateReview(1L, request)).thenThrow(new ResourceNotFoundException("Not found"));

        ResponseEntity<ApiResponse> response = reviewController.updateReview(request, 1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void testDeleteReview_success() {
        ResponseEntity<ApiResponse> response = reviewController.deleteReview(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.REVIEW_DELETE_SUCCESS, response.getBody().getMessage());
        verify(reviewService).deleteReview(1L);
    }

    @Test
    void testDeleteReview_notFound() {
        doThrow(new ResourceNotFoundException("Missing"))
                .when(reviewService).deleteReview(1L);

        ResponseEntity<ApiResponse> response = reviewController.deleteReview(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Missing", response.getBody().getMessage());
    }

    @Test
    void testGetReviewsByUserID_success() {
        Review review1 = new Review();
        Review review2 = new Review();
        Page<Review> page = new PageImpl<>(List.of(review1, review2));

        ReviewDto dto1 = new ReviewDto();
        ReviewDto dto2 = new ReviewDto();

        when(reviewService.findAllReviewsByUserId(10L, 0, 5)).thenReturn(page);
        when(modelMapper.map(review1, ReviewDto.class)).thenReturn(dto1);
        when(modelMapper.map(review2, ReviewDto.class)).thenReturn(dto2);

        ResponseEntity<ApiResponse> response = reviewController.getReviewsByUserID(10L, 0, 5);

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.REVIEW_FOUND, response.getBody().getMessage());
        Page<?> returnedPage = (Page<?>) response.getBody().getData();
        assertEquals(2, returnedPage.getContent().size());
    }

    @Test
    void testGetAverageRatingForVet_success() {
        when(reviewService.getAverageRatingForVet(99L)).thenReturn(4.5);

        ResponseEntity<ApiResponse> response = reviewController.getAverageRatingForVet(99L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(4.5, response.getBody().getData());
        assertEquals(FeedBackMessage.REVIEW_FOUND, response.getBody().getMessage());
    }
}
