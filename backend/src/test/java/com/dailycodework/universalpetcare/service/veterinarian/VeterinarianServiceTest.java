package com.dailycodework.universalpetcare.service.veterinarian;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDto;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Veterinarian;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.ReviewRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
import com.dailycodework.universalpetcare.service.photo.PhotoService;
import com.dailycodework.universalpetcare.service.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeterinarianServiceTest {

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityConverter<Veterinarian, UserDto> entityConverter;

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PhotoService photoService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private VeterinarianService veterinarianService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllVeterinariansWithDetails_returnsMappedList() throws SQLException {
        Veterinarian vet = mock(Veterinarian.class);
        vet.setId(1L);
        byte[] photoBytes = new byte[]{1, 2, 3};

        UserDto dto = new UserDto();
        dto.setId(1L);

        when(userRepository.findAllByUserType("VET")).thenReturn(List.of(vet));
        when(entityConverter.mapEntityToDto(vet, UserDto.class)).thenReturn(dto);
        when(reviewService.getAverageRatingForVet(1L)).thenReturn(4.2);
        when(reviewRepository.countByVeterinarianId(1L)).thenReturn(3L);
        when(vet.getPhoto()).thenReturn(null);

        List<UserDto> vets = veterinarianService.getAllVeterinariansWithDetails();

        assertEquals(1, vets.size());
        assertEquals(dto, vets.get(0));
        assertEquals(4.2, vets.get(0).getAverageRating());
        assertEquals(3L, vets.get(0).getTotalReviewers());
    }

    @Test
    void testGetAllVeterinariansWithDetails_photoPresent_setsPhotoBytes() throws SQLException {
        Veterinarian vet = new Veterinarian();
        vet.setId(1L);
        PhotoService photoServiceSpy = spy(photoService);

        when(userRepository.findAllByUserType("VET")).thenReturn(List.of(vet));
        when(entityConverter.mapEntityToDto(vet, UserDto.class)).thenReturn(new UserDto());
        when(reviewService.getAverageRatingForVet(anyLong())).thenReturn(4.5);
        when(reviewRepository.countByVeterinarianId(anyLong())).thenReturn(2L);

        // Setup veterinarian photo with id
        var photo = mock(com.dailycodework.universalpetcare.model.Photo.class);
        vet.setPhoto(photo);
        when(photo.getId()).thenReturn(5L);
        when(photoService.getImageData(5L)).thenReturn(new byte[]{1, 2, 3});

        List<UserDto> vets = veterinarianService.getAllVeterinariansWithDetails();

        verify(photoService).getImageData(5L);
        assertNotNull(vets.get(0).getPhoto());
    }

    @Test
    void testGetSpecializations_returnsList() {
        List<String> specializations = List.of("Surgery", "Dermatology");
        when(veterinarianRepository.getSpecializations()).thenReturn(specializations);

        List<String> result = veterinarianService.getSpecializations();

        assertEquals(specializations, result);
    }

    @Test
    void testFindAvailableVetsForAppointment_filtersCorrectly() {
        String specialization = "Surgery";
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        Veterinarian vet1 = new Veterinarian();
        vet1.setId(1L);
        Veterinarian vet2 = new Veterinarian();
        vet2.setId(2L);

        when(veterinarianRepository.existsBySpecialization(specialization)).thenReturn(true);
        when(veterinarianRepository.findBySpecialization(specialization)).thenReturn(List.of(vet1, vet2));
        when(userRepository.findAllByUserType("VET")).thenReturn(List.of(vet1, vet2));
        when(entityConverter.mapEntityToDto(any(Veterinarian.class), eq(UserDto.class)))
                .thenAnswer(invocation -> new UserDto());

        // No conflicting appointments for vet1 and vet2
        when(appointmentRepository.findByVeterinarianAndAppointmentDate(any(), eq(date))).thenReturn(Collections.emptyList());

        List<UserDto> availableVets = veterinarianService.findAvailableVetsForAppointment(specialization, date, time);

        assertEquals(2, availableVets.size());
    }

    @Test
    void testFindAvailableVetsForAppointment_withConflictingAppointment_filtersOut() {
        String specialization = "Surgery";
        LocalDate date = LocalDate.now();
        LocalTime requestedTime = LocalTime.of(10, 0);

        Veterinarian vet = new Veterinarian();
        vet.setId(1L);

        Appointment conflictingAppointment = new Appointment();
        conflictingAppointment.setAppointmentTime(LocalTime.of(9, 0));

        when(veterinarianRepository.existsBySpecialization(specialization)).thenReturn(true);
        when(veterinarianRepository.findBySpecialization(specialization)).thenReturn(List.of(vet));
        when(appointmentRepository.findByVeterinarianAndAppointmentDate(vet, date)).thenReturn(List.of(conflictingAppointment));
        when(entityConverter.mapEntityToDto(any(), eq(UserDto.class))).thenReturn(new UserDto());

        List<UserDto> availableVets = veterinarianService.findAvailableVetsForAppointment(specialization, date, requestedTime);

        // Because of overlapping, no vets available
        assertEquals(0, availableVets.size());
    }

    @Test
    void testGetVeterinariansBySpecialization_success() {
        String specialization = "Surgery";
        Veterinarian vet = new Veterinarian();

        when(veterinarianRepository.existsBySpecialization(specialization)).thenReturn(true);
        when(veterinarianRepository.findBySpecialization(specialization)).thenReturn(List.of(vet));

        List<Veterinarian> vets = veterinarianService.getVeterinariansBySpecialization(specialization);

        assertEquals(1, vets.size());
        assertEquals(vet, vets.get(0));
    }

    @Test
    void testGetVeterinariansBySpecialization_notFound_throws() {
        String specialization = "Unknown";

        when(veterinarianRepository.existsBySpecialization(specialization)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> veterinarianService.getVeterinariansBySpecialization(specialization));
    }

    @Test
    void testAggregateVetsBySpecialization() {
        Object[] record1 = new Object[]{"Surgery", 5L};
        Object[] record2 = new Object[]{"Dentistry", 3L};
        List<Object[]> results = List.of(record1, record2);

        when(veterinarianRepository.countVetsBySpecialization()).thenReturn(results);

        var aggregate = veterinarianService.aggregateVetsBySpecialization();

        assertEquals(2, aggregate.size());
        assertEquals("Surgery", aggregate.get(0).get("specialization"));
        assertEquals(5L, aggregate.get(0).get("count"));
        assertEquals("Dentistry", aggregate.get(1).get("specialization"));
        assertEquals(3L, aggregate.get(1).get("count"));
    }
}
