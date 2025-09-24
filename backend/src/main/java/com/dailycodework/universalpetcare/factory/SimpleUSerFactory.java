package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.exception.AlreadyExistsException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SimpleUSerFactory implements UserFactory {
    private final UserRepository userRepository;
    private final VeterinarianFactory veterinarianFactory;
    private final PatientFactory patientFactory;
    private final AdminFactory adminFactory;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new AlreadyExistsException("Oops! " + registrationRequest.getEmail() + " already exists!");
        }

        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        String normalizedType = Optional.ofNullable(registrationRequest.getUserType())
                .map(String::trim)
                .filter(type -> !type.isEmpty())
                .map(type -> type.toUpperCase(Locale.ENGLISH))
                .orElseThrow(() -> new IllegalArgumentException("User type is required"));

        registrationRequest.setUserType(normalizedType);

        return switch (normalizedType) {
            case "VET" -> veterinarianFactory.createVeterinarian(registrationRequest);
            case "PATIENT" -> patientFactory.createPatient(registrationRequest);
            case "ADMIN" -> adminFactory.createAdmin(registrationRequest);
            default -> throw new IllegalArgumentException("Unsupported user type: " + normalizedType);
        };
    }
}
