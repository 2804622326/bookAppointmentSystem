package com.dailycodework.universalpetcare.service.pet;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Pet;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.PetRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService implements IPetService {
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<Pet> savePetsForAppointment(List<Pet> pets) {
        return petRepository.saveAll(pets);
    }

    @Override
    public List<Pet> savePetsForAppointment(Long appointmentId, List<Pet> pets) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(null);
        return pets.stream()
                .peek(pet -> pet.setAppointment(appointment))
                .map(petRepository::save)
                .collect(Collectors.toList());
    }


    @Override
    public Pet updatePet(Pet pet, Long petId) {
        Pet existingPet = getPetById(petId);
        existingPet.setName(pet.getName());
        existingPet.setAge(pet.getAge());
        existingPet.setColor(pet.getColor());
        existingPet.setType(pet.getType());
        existingPet.setBreed(pet.getBreed());
        existingPet.setAge(pet.getAge());
        return petRepository.save(existingPet);
    }

    @Override
    public void deletePet(Long petId) {
        petRepository.findById(petId)
                .ifPresentOrElse(petRepository::delete,
                        () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
                        });

    }

    @Override
    public Pet getPetById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
    }

    @Override
    public List<String> getPetTypes(){
        return petRepository.getDistinctPetTypes();
    }

    @Override
    public List<String> getPetColors(){
        return petRepository.getDistinctPetColors();
    }

    @Override
    public List<String> getPetBreeds(String petType){
        return petRepository.getDistinctPetBreedsByPetType(petType);
    }

}
