package com.dailycodework.universalpetcare.config;

import com.dailycodework.universalpetcare.dto.ReviewDto;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        String fullName = Stream.of(user.getFirstName(), user.getLastName())
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(" "));
        return fullName.isEmpty() ? null : fullName;
    }
}
