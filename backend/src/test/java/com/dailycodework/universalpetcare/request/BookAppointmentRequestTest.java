package com.dailycodework.universalpetcare.request;

import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookAppointmentRequestTest {

    @Test
    void testBookAppointmentRequestFields() {
        Appointment appointment = new Appointment();
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        List<Pet> pets = List.of(pet1, pet2);

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setAppointment(appointment);
        request.setPets(pets);

        assertEquals(appointment, request.getAppointment());
        assertEquals(pets, request.getPets());
        assertEquals(2, request.getPets().size());
    }
}