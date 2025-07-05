package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.dto.AppointmentDto;
import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.ReviewDto;
import com.dailycodework.universalpetcare.dto.UserDto;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.factory.UserFactory;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Photo;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.ReviewRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;
import com.dailycodework.universalpetcare.service.appointment.AppointmentService;
import com.dailycodework.universalpetcare.service.pet.IPetService;
import com.dailycodework.universalpetcare.service.photo.PhotoService;
import com.dailycodework.universalpetcare.service.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserFactory userFactory;
    @Mock
    private VeterinarianRepository veterinarianRepository;
    @Mock
    private EntityConverter<User, UserDto> entityConverter;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private IPetService petService;
    @Mock
    private PhotoService photoService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_callsFactory() {
        RegistrationRequest req = new RegistrationRequest();
        User createdUser = new User();
        when(userFactory.createUser(req)).thenReturn(createdUser);

        User result = userService.register(req);

        assertEquals(createdUser, result);
    }

    @Test
    void testUpdate_success() {
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setGender("M");
        request.setPhoneNumber("123456");
        request.setSpecialization("Vet");

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.update(userId, request);

        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals("M", updated.getGender());
        assertEquals("123456", updated.getPhoneNumber());
        assertEquals("Vet", updated.getSpecialization());
    }

    @Test
    void testFindById_found() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertEquals(user, result);
    }

    @Test
    void testFindById_notFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(2L));
    }

    @Test
    void testDelete_success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());

        userService.delete(1L);

        verify(reviewRepository).deleteAll(Collections.emptyList());
        verify(appointmentRepository).deleteAll(Collections.emptyList());
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDelete_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        User user2 = new User();
        UserDto dto1 = new UserDto();
        UserDto dto2 = new UserDto();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(entityConverter.mapEntityToDto(user1, UserDto.class)).thenReturn(dto1);
        when(entityConverter.mapEntityToDto(user2, UserDto.class)).thenReturn(dto2);

        List<UserDto> dtos = userService.getAllUsers();

        assertEquals(2, dtos.size());
        assertTrue(dtos.contains(dto1));
        assertTrue(dtos.contains(dto2));
    }

    @Test
    void testGetUserWithDetails() throws SQLException {
        User user = new User();
        user.setId(1L);
        Photo photo = new Photo();
        photo.setId(10L);
        user.setPhoto(photo);

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Review review = new Review();
        review.setId(100L);
        review.setStars(5);
        List<Review> reviews = List.of(review);
        Page<Review> reviewPage = new PageImpl<>(reviews);

        AppointmentDto appointmentDto = new AppointmentDto();
        List<AppointmentDto> appointments = List.of(appointmentDto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(entityConverter.mapEntityToDto(user, UserDto.class)).thenReturn(userDto);
        when(reviewRepository.countByVeterinarianId(1L)).thenReturn(3L);
        when(appointmentService.getUserAppointments(1L)).thenReturn(appointments);
        when(photoService.getImageData(10L)).thenReturn(new byte[]{1,2,3});
        when(reviewService.findAllReviewsByUserId(1L, 0, Integer.MAX_VALUE)).thenReturn(reviewPage);
        when(reviewService.getAverageRatingForVet(1L)).thenReturn(4.5);

        UserDto result = userService.getUserWithDetails(1L);

        assertEquals(1L, result.getId());
        assertEquals(3L, result.getTotalReviewers());
        assertEquals(4.5, result.getAverageRating());
        assertArrayEquals(new byte[]{1,2,3}, result.getPhoto());
        assertEquals(appointments, result.getAppointments());
        assertEquals(1, result.getReviews().size());
    }

    @Test
    void testCountMethods() {
        when(userRepository.countByUserType("VET")).thenReturn(5L);
        when(userRepository.countByUserType("PATIENT")).thenReturn(10L);
        when(userRepository.count()).thenReturn(15L);

        assertEquals(5L, userService.countVeterinarians());
        assertEquals(10L, userService.countPatients());
        assertEquals(15L, userService.countAllUsers());
    }

    @Test
    void testAggregateUsersByMonthAndType() {
        User user = new User();
        user.setCreatedAt(LocalDate.from(LocalDateTime.of(2023, 5, 15, 0, 0)));
        user.setUserType("VET");

        when(userRepository.findAll()).thenReturn(List.of(user));

        Map<String, Map<String, Long>> result = userService.aggregateUsersByMonthAndType();

        assertTrue(result.containsKey("May"));
        assertTrue(result.get("May").containsKey("VET"));
        assertEquals(1L, result.get("May").get("VET"));
    }

    @Test
    void testAggregateUsersByEnabledStatusAndType() {
        User user = new User();
        user.setEnabled(true);
        user.setUserType("PATIENT");

        when(userRepository.findAll()).thenReturn(List.of(user));

        Map<String, Map<String, Long>> result = userService.aggregateUsersByEnabledStatusAndType();

        assertTrue(result.containsKey("Enabled"));
        assertTrue(result.get("Enabled").containsKey("PATIENT"));
        assertEquals(1L, result.get("Enabled").get("PATIENT"));
    }

    @Test
    void testLockAndUnlockUserAccount() {
        userService.lockUserAccount(1L);
        verify(userRepository).updateUserEnabledStatus(1L, false);

        userService.unLockUserAccount(2L);
        verify(userRepository).updateUserEnabledStatus(2L, true);
    }
}
