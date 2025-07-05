package com.dailycodework.universalpetcare.event;

import com.dailycodework.universalpetcare.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationCompleteEventTest {

    @Test
    void testRegistrationCompleteEvent() {
        User user = new User();
        RegistrationCompleteEvent event = new RegistrationCompleteEvent(user);

        assertEquals(user, event.getUser());
        assertEquals(user, event.getSource());
    }
}