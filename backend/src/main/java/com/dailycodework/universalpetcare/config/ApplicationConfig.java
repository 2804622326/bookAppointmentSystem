package com.dailycodework.universalpetcare.config;


import com.dailycodework.universalpetcare.dto.ReviewDto;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        Converter<User, String> fullNameConverter = context -> toFullName(context.getSource());

        modelMapper.typeMap(Review.class, ReviewDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> toUserId(src.getPatient()), ReviewDto::setPatientId);
                    mapper.map(src -> toUserId(src.getVeterinarian()), ReviewDto::setVeterinarianId);
                    mapper.using(fullNameConverter).map(Review::getPatient, ReviewDto::setPatientName);
                    mapper.using(fullNameConverter).map(Review::getVeterinarian, ReviewDto::setVeterinarianName);
                });

        return modelMapper;
    }

    private Long toUserId(User user) {
        return user != null ? user.getId() : null;
    }

    private String toFullName(User user) {
        if (user == null) {
            return null;
        }
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
