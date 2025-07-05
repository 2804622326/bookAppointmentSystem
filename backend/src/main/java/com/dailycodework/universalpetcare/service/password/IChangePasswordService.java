package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.request.ChangePasswordRequest;

public interface IChangePasswordService {
    void changePassword(Long userId, ChangePasswordRequest request);
}
