package com.dailycodework.universalpetcare.service.appointment;

import com.dailycodework.universalpetcare.dto.AppointmentDto;
import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.PetDto;
import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.AppointmentUpdateRequest;
import com.dailycodework.universalpetcare.request.BookAppointmentRequest;
import com.dailycodework.universalpetcare.service.pet.IPetService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IPetService petService;

    @Mock
    private EntityConverter<Appointment, AppointmentDto> appointmentConverter;

    @Mock
    private EntityConverter<Pet, PetDto> petConverter;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAppointment_success() {
        Long senderId = 1L;
        Long recipientId = 2L;
        User sender = new User();
        User recipient = new User();

        Appointment appointment = new Appointment();
        List<Pet> pets = List.of(new Pet());

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setAppointment(appointment);
        request.setPets(pets);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(petService.savePetsForAppointment(pets)).thenReturn(pets);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.createAppointment(request, senderId, recipientId);

        assertNotNull(result);
        assertEquals(AppointmentStatus.WAITING_FOR_APPROVAL, result.getStatus());
    }

    @Test
    void testCreateAppointment_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        BookAppointmentRequest request = new BookAppointmentRequest();
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.createAppointment(request, 1L, 2L));
    }

    @Test
    void testUpdateAppointment_success() {
        Appointment existing = new Appointment();
        existing.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
        existing.setId(10L);

        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        request.setAppointmentDate("2024-12-01");
        request.setAppointmentTime("10:00");
        request.setReason("Vaccination");

        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(appointmentRepository.save(any())).thenReturn(existing);

        Appointment updated = appointmentService.updateAppointment(10L, request);

        assertEquals(LocalDate.parse("2024-12-01"), updated.getAppointmentDate());
        assertEquals(LocalTime.parse("10:00"), updated.getAppointmentTime());
        assertEquals("Vaccination", updated.getReason());
    }

    @Test
    void testUpdateAppointment_statusNotAllowed() {
        Appointment existing = new Appointment();
        existing.setStatus(AppointmentStatus.APPROVED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing));

        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        assertThrows(IllegalStateException.class, () -> appointmentService.updateAppointment(1L, request));
    }

    @Test
    void testCancelAppointment_success() {
        Appointment appt = new Appointment();
        appt.setId(1L);
        appt.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepository.saveAndFlush(appt)).thenReturn(appt);

        Appointment result = appointmentService.cancelAppointment(1L);

        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testCancelAppointment_invalidStatus() {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.APPROVED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));

        assertThrows(IllegalStateException.class, () -> appointmentService.cancelAppointment(1L));
    }

    @Test
    void testApproveAppointment_success() {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepository.saveAndFlush(appt)).thenReturn(appt);

        Appointment result = appointmentService.approveAppointment(1L);

        assertEquals(AppointmentStatus.APPROVED, result.getStatus());
    }

    @Test
    void testDeclineAppointment_success() {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepository.saveAndFlush(appt)).thenReturn(appt);

        Appointment result = appointmentService.declineAppointment(1L);

        assertEquals(AppointmentStatus.NOT_APPROVED, result.getStatus());
    }

    @Test
    void testGetAppointmentSummary() {
        Appointment a1 = new Appointment();
        a1.setStatus(AppointmentStatus.APPROVED);
        Appointment a2 = new Appointment();
        a2.setStatus(AppointmentStatus.APPROVED);
        Appointment a3 = new Appointment();
        a3.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findAll()).thenReturn(List.of(a1, a2, a3));

        List<Map<String, Object>> summary = appointmentService.getAppointmentSummary();

        assertEquals(2, summary.size());
        assertTrue(summary.stream().anyMatch(m -> m.get("name").equals("approved")));
        assertTrue(summary.stream().anyMatch(m -> m.get("name").equals("cancelled")));
    }

    @Test
    void testDeleteAppointment_found() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void testDeleteAppointment_notFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.deleteAppointment(1L));
    }

    @Test
    void testApproveAppointment_invalidStatusThrows() {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.APPROVED);

        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(appt));

        assertThrows(IllegalStateException.class, () -> appointmentService.approveAppointment(5L));
    }

    @Test
    void testDeclineAppointment_invalidStatusThrows() {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(6L)).thenReturn(Optional.of(appt));

        assertThrows(IllegalStateException.class, () -> appointmentService.declineAppointment(6L));
    }

    @Test
    void testGetUserAppointments_mapsAppointmentAndPets() {
        Appointment appointment = new Appointment();
        Pet pet = new Pet();
        appointment.setPets(List.of(pet));
        AppointmentDto dto = new AppointmentDto();
        PetDto petDto = new PetDto();

        when(appointmentRepository.findAllByUserId(3L)).thenReturn(List.of(appointment));
        when(appointmentConverter.mapEntityToDto(appointment, AppointmentDto.class)).thenReturn(dto);
        when(petConverter.mapEntityToDto(pet, PetDto.class)).thenReturn(petDto);

        List<AppointmentDto> results = appointmentService.getUserAppointments(3L);

        assertEquals(1, results.size());
        assertEquals(List.of(petDto), results.get(0).getPets());
    }

    @Test
    void testGetAppointmentIds_returnsIds() {
        Appointment a1 = new Appointment();
        a1.setId(11L);
        Appointment a2 = new Appointment();
        a2.setId(12L);

        when(appointmentRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Long> ids = appointmentService.getAppointmentIds();

        assertEquals(List.of(11L, 12L), ids);
    }

    @Test
    void testSetAppointmentStatus_transitionsApprovedToUpcoming() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.now().plusMinutes(10));

        when(appointmentRepository.findById(20L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        appointmentService.setAppointmentStatus(20L);

        assertEquals(AppointmentStatus.UP_COMING, appointment.getStatus());
    }

    @Test
    void testSetAppointmentStatus_transitionsUpcomingToOngoing() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.UP_COMING);
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setAppointmentTime(LocalTime.now().minusMinutes(1).truncatedTo(ChronoUnit.MINUTES));

        when(appointmentRepository.findById(21L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        appointmentService.setAppointmentStatus(21L);

        assertEquals(AppointmentStatus.ON_GOING, appointment.getStatus());
    }

    @Test
    void testSetAppointmentStatus_transitionsOngoingToCompleted() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.ON_GOING);
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));
        appointment.setAppointmentTime(LocalTime.now().minusHours(1));

        when(appointmentRepository.findById(22L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        appointmentService.setAppointmentStatus(22L);

        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
    }

    @Test
    void testSetAppointmentStatus_waitingPastAppointmentBecomesNotApproved() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));
        appointment.setAppointmentTime(LocalTime.now().minusHours(3));

        when(appointmentRepository.findById(23L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        appointmentService.setAppointmentStatus(23L);

        assertEquals(AppointmentStatus.NOT_APPROVED, appointment.getStatus());
    }
}
