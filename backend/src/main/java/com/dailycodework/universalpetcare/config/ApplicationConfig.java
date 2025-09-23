package com.dailycodework.universalpetcare.config;

import com.dailycodework.universalpetcare.dto.ReviewDto;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<Review, ReviewDto> reviewTypeMap = modelMapper.createTypeMap(Review.class, ReviewDto.class);
        reviewTypeMap.addMappings(mapper -> {
            mapper.map(src -> toUserId(src.getPatient()), ReviewDto::setPatientId);
            mapper.map(src -> toUserId(src.getVeterinarian()), ReviewDto::setVeterinarianId);
            mapper.skip(ReviewDto::setPatientName);
            mapper.skip(ReviewDto::setVeterinarianName);
        });
        reviewTypeMap.setPostConverter(context -> {
            Review source = context.getSource();
            ReviewDto destination = context.getDestination();
            if (source == null || destination == null) {
                return destination;
            }
            destination.setPatientName(toFullName(source.getPatient()));
            destination.setVeterinarianName(toFullName(source.getVeterinarian()));
            return destination;
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
