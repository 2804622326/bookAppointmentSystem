package com.dailycodework.universalpetcare.service.review;

import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.exception.AlreadyExistsException;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.ReviewRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.ReviewUpdateRequest;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveReview_success() {
        Long reviewerId = 1L;
        Long vetId = 2L;

        Review review = new Review();
        User reviewer = new User();
        User vet = new User();

        when(reviewRepository.findByVeterinarianIdAndPatientId(vetId, reviewerId)).thenReturn(Optional.empty());
        when(appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(vetId, reviewerId, AppointmentStatus.COMPLETED))
                .thenReturn(true);
        when(userRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
        when(userRepository.findById(vetId)).thenReturn(Optional.of(vet));
        when(reviewRepository.save(any())).thenReturn(review);

        Review result = reviewService.saveReview(review, reviewerId, vetId);

        assertNotNull(result);
        verify(reviewRepository).save(review);
    }

    @Test
    void testSaveReview_sameReviewerAndVet_throws() {
        Long sameId = 1L;
        Review review = new Review();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review, sameId, sameId));

        assertEquals(FeedBackMessage.CANNOT_REVIEW, ex.getMessage());
    }

    @Test
    void testSaveReview_alreadyExists_throws() {
        Long reviewerId = 1L;
        Long vetId = 2L;
        Review review = new Review();

        when(reviewRepository.findByVeterinarianIdAndPatientId(vetId, reviewerId))
                .thenReturn(Optional.of(new Review()));

        assertThrows(AlreadyExistsException.class,
                () -> reviewService.saveReview(review, reviewerId, vetId));
    }

    @Test
    void testSaveReview_noCompletedAppointment_throws() {
        Long reviewerId = 1L;
        Long vetId = 2L;
        Review review = new Review();

        when(reviewRepository.findByVeterinarianIdAndPatientId(vetId, reviewerId)).thenReturn(Optional.empty());
        when(appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(vetId, reviewerId, AppointmentStatus.COMPLETED))
                .thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> reviewService.saveReview(review, reviewerId, vetId));

        assertEquals(FeedBackMessage.NOT_ALLOWED, ex.getMessage());
    }

    @Test
    void testSaveReview_userNotFound_throws() {
        Long reviewerId = 1L;
        Long vetId = 2L;
        Review review = new Review();

        when(reviewRepository.findByVeterinarianIdAndPatientId(vetId, reviewerId)).thenReturn(Optional.empty());
        when(appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(vetId, reviewerId, AppointmentStatus.COMPLETED))
                .thenReturn(true);
        when(userRepository.findById(vetId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.saveReview(review, reviewerId, vetId));
    }

    @Test
    void testUpdateReview_success() {
        Long reviewId = 10L;
        ReviewUpdateRequest request = new ReviewUpdateRequest();
        request.setStars(5);
        request.setFeedback("Great");

        Review existing = new Review();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(existing)).thenReturn(existing);

        Review result = reviewService.updateReview(reviewId, request);

        assertEquals(5, result.getStars());
        assertEquals("Great", result.getFeedback());
    }

    @Test
    void testUpdateReview_notFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReview(99L, new ReviewUpdateRequest()));
    }

    @Test
    void testGetAverageRatingForVet_hasRatings() {
        List<Review> reviews = List.of(
                createReviewWithStars(4),
                createReviewWithStars(5)
        );
        when(reviewRepository.findByVeterinarianId(1L)).thenReturn(reviews);

        double avg = reviewService.getAverageRatingForVet(1L);

        assertEquals(4.5, avg);
    }

    @Test
    void testGetAverageRatingForVet_noReviews() {
        when(reviewRepository.findByVeterinarianId(1L)).thenReturn(List.of());

        double avg = reviewService.getAverageRatingForVet(1L);

        assertEquals(0, avg);
    }

    @Test
    void testFindAllReviewsByUserId() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Review> page = new PageImpl<>(List.of(new Review()));

        when(reviewRepository.findAllByUserId(1L, pageRequest)).thenReturn(page);

        Page<Review> result = reviewService.findAllReviewsByUserId(1L, 0, 5);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void testDeleteReview_success() {
        Long reviewId = 1L;
        Review review = mock(Review.class);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReview(reviewId);

        verify(review).removeRelationShip();
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void testDeleteReview_notFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.deleteReview(1L));
    }

    private Review createReviewWithStars(int stars) {
        Review review = new Review();
        review.setStars(stars);
        return review;
    }
}
