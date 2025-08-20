package com.dailycodework.universalpetcare.factory;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void testUserFactoryImplementation() {
        UserFactory factory = registrationRequest -> {
            User user = new User();
            user.setFirstName(registrationRequest.getFirstName());
            return user;
        };

        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("John");

        User user = factory.createUser(request);

        assertEquals("John", user.getFirstName());
    }
}