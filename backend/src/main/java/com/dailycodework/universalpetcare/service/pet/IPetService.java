package com.dailycodework.universalpetcare.service.pet;

import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;

import java.util.List;

public interface IPetService {
    List<Pet> savePetsForAppointment(List<Pet> pets);

    List<Pet> savePetsForAppointment(Long appointmentId, List<Pet> pets);

   // List<Pet> savePetsForAppointment(Appointment appointment, List<Pet> pets);

    Pet updatePet(Pet pet, Long id);
    void deletePet(Long id);
    Pet getPetById(Long id);

    List<String> getPetTypes();

    List<String> getPetColors();

    List<String> getPetBreeds(String petType);
}
