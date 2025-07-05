package com.dailycodework.universalpetcare.email;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailPropertiesTest {

    @Test
    void testDefaultEmailProperties() {
        assertEquals("smtp.gmail.com", EmailProperties.DEFAULT_HOST);
        assertEquals(587, EmailProperties.DEFAULT_PORT);
        assertEquals("petuniversal8@gmail.com", EmailProperties.DEFAULT_SENDER);
        assertEquals("petuniversal8@gmail.com", EmailProperties.DEFAULT_USERNAME);
        assertEquals("rxuihkhrsdxxugfv", EmailProperties.DEFAULT_PASSWORD);
        assertTrue(EmailProperties.DEFAULT_AUTH);
        assertTrue(EmailProperties.DEFAULT_STARTTLS);
    }
}