package com.dailycodework.universalpetcare.utils;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SystemUtilsTest {

    @Test
    void testGetExpirationTime_shouldBeTwoMinutesAhead() {
        Date now = new Date();
        Date expiration = SystemUtils.getExpirationTime();

        long differenceInMillis = expiration.getTime() - now.getTime();
        long differenceInMinutes = differenceInMillis / (60 * 1000);

        assertTrue(differenceInMinutes == 2 || differenceInMinutes == 1,
                "Expiration time should be approximately 2 minutes from now.");
    }
}
