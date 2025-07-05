package com.dailycodework.universalpetcare.service.patient;

import com.dailycodework.universalpetcare.dto.UserDto;

import java.util.List;

public interface IPatientService {
    List<UserDto> getPatients();
}
