package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.model.User;

import java.util.Optional;

public interface IPasswordResetService {

    Optional<User> findUserByPasswordResetToken(String token);
    void requestPasswordReset(String email);
    String resetPassword(String password, User user);
}
