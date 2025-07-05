package com.dailycodework.universalpetcare.service.veterinarian;

import com.dailycodework.universalpetcare.dto.UserDto;
import com.dailycodework.universalpetcare.model.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface IVeterinarianService {
    List<UserDto> getAllVeterinariansWithDetails();

    List<String> getSpecializations();

    List<UserDto> findAvailableVetsForAppointment(String specialization, LocalDate date, LocalTime time);

    List<Veterinarian> getVeterinariansBySpecialization(String specialization);

    List<Map<String, Object>> aggregateVetsBySpecialization();
}
