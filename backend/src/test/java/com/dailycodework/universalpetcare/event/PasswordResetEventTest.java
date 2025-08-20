package com.dailycodework.universalpetcare.event;

import com.dailycodework.universalpetcare.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetEventTest {

    @Test
    void testPasswordResetEvent() {
        User user = new User();
        Object source = new Object();
        PasswordResetEvent event = new PasswordResetEvent(source, user);

        assertEquals(user, event.getUser());
        assertEquals(source, event.getSource());
    }
}