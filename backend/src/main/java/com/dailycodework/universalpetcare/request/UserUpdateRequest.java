package com.dailycodework.universalpetcare.request;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private String specialization;
}
