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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IPetService petService;
    private final EntityConverter<Appointment, AppointmentDto> entityConverter;
    private final EntityConverter<Pet, PetDto> petEntityConverter;



    @Transactional
    @Override
    public Appointment createAppointment(BookAppointmentRequest request, Long senderId, Long recipientId) {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> recipient = userRepository.findById(recipientId);
        if (sender.isPresent() && recipient.isPresent()) {

            Appointment appointment = request.getAppointment();
            List<Pet> pets = request.getPets();
            pets.forEach(pet -> pet.setAppointment(appointment));
            List<Pet> savedPets = petService.savePetsForAppointment(pets);
            appointment.setPets(savedPets);

            appointment.addPatient(sender.get());
            appointment.addVeterinarian(recipient.get());
            appointment.setAppointmentNo();
            appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
            return appointmentRepository.save(appointment);
        }
        throw new ResourceNotFoundException(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment updateAppointment(Long id, AppointmentUpdateRequest request) {
        Appointment existingAppointment = getAppointmentById(id);
        if (!Objects.equals(existingAppointment.getStatus(), AppointmentStatus.WAITING_FOR_APPROVAL)) {
            throw new IllegalStateException(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED);
        }
        existingAppointment.setAppointmentDate(LocalDate.parse(request.getAppointmentDate()));
        existingAppointment.setAppointmentTime(LocalTime.parse(request.getAppointmentTime()));
        existingAppointment.setReason(request.getReason());
        return appointmentRepository.save(existingAppointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.findById(id)
                .ifPresentOrElse(appointmentRepository::delete, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND);
                });

    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND));
    }

    @Override
    public Appointment getAppointmentByNo(String appointmentNo) {
        return appointmentRepository.findByAppointmentNo(appointmentNo);
    }

    @Override
    public List<AppointmentDto> getUserAppointments(Long userId) {
        List<Appointment> appointments = appointmentRepository.findAllByUserId(userId);
        return appointments.stream()
                .map(appointment -> {
                    AppointmentDto appointmentDto = entityConverter.mapEntityToDto(appointment, AppointmentDto.class);
                    List<PetDto> petDto = appointment.getPets()
                            .stream()
                            .map(pet -> petEntityConverter.mapEntityToDto(pet, PetDto.class)).toList();
                    appointmentDto.setPets(petDto);
                    return appointmentDto;
                }).toList();
    }

    @Override
    public  Appointment cancelAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {appointment.setStatus(AppointmentStatus.CANCELLED);
                    return appointmentRepository.saveAndFlush(appointment);
                    }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED));

    }

    @Override
    public  Appointment approveAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {appointment.setStatus(AppointmentStatus.APPROVED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));

    }


    @Override
    public  Appointment declineAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));

    }

    @Override
    public long countAppointment(){
        return appointmentRepository.count();
    }

    @Override
    public List<Map<String, Object>> getAppointmentSummary() {
        return getAllAppointments()
                .stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> createStatusSummaryMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    

    private Map<String, Object> createStatusSummaryMap(AppointmentStatus status, Long value){
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("name", formatAppointmentStatus(status));
        summaryMap.put("value", value);
        return summaryMap;
    }

    private String formatAppointmentStatus(AppointmentStatus appointmentStatus) {
        return appointmentStatus.toString().replace("_", "-").toLowerCase();
    }

    @Override
        public List<Long> getAppointmentIds() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return  appointments.stream()
                .map(Appointment::getId)
                .collect(Collectors.toList());
        }


    @Override
    public void setAppointmentStatus(Long appointmentId){
        Appointment appointment = getAppointmentById(appointmentId);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime appointmentEndTime = appointment.getAppointmentTime()
                .plusMinutes(2).truncatedTo(ChronoUnit.MINUTES);

        switch (appointment.getStatus()) {
            case APPROVED:
                if (currentDate.isBefore(appointment.getAppointmentDate()) ||
                        (currentDate.equals(appointment.getAppointmentDate()) && currentTime.isBefore(appointment.getAppointmentTime()))) {
                    appointment.setStatus(AppointmentStatus.UP_COMING);
                    // If already UP_COMING, no change needed.
                }
                break;

            case UP_COMING:
                if (currentDate.equals(appointment.getAppointmentDate()) &&
                        currentTime.isAfter(appointment.getAppointmentTime()) && !currentTime.isAfter(appointmentEndTime)) {
                    // Changed to include the end time as part of ON_GOING status
                    appointment.setStatus(AppointmentStatus.ON_GOING);
                }
                break;
            case ON_GOING:
                if (currentDate.isAfter(appointment.getAppointmentDate()) ||
                        (currentDate.equals(appointment.getAppointmentDate()) && !currentTime.isBefore(appointmentEndTime))) {
                    // Changed to mark as COMPLETED when current time is not before the end time
                    appointment.setStatus(AppointmentStatus.COMPLETED);
                }
                break;

            case WAITING_FOR_APPROVAL:
                if (currentDate.isAfter(appointment.getAppointmentDate()) ||
                        (currentDate.equals(appointment.getAppointmentDate()) && currentTime.isAfter(appointment.getAppointmentTime()))) {
                    // Adjusted to change status to NOT_APPROVED if current time is past the appointment time
                    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                }
                break;
        }
        appointmentRepository.save(appointment);
        
    }

}
