package com.dailycodework.universalpetcare.config;

import com.dailycodework.universalpetcare.dto.ReviewDto;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true);

        TypeMap<Review, ReviewDto> reviewTypeMap = modelMapper.typeMap(Review.class, ReviewDto.class);
        reviewTypeMap.addMappings(mapper -> {
            mapper.map(src -> toUserId(src.getPatient()), ReviewDto::setPatientId);
            mapper.map(src -> toUserId(src.getVeterinarian()), ReviewDto::setVeterinarianId);
            mapper.skip(ReviewDto::setPatientName);
            mapper.skip(ReviewDto::setVeterinarianName);
        }).setPostConverter(context -> {
            Review source = context.getSource();
            ReviewDto destination = context.getDestination();
            if (source == null || destination == null) {
                return destination;
            }
            destination.setPatientName(fullName(source.getPatient()));
            destination.setVeterinarianName(fullName(source.getVeterinarian()));
            return destination;
        });

        return modelMapper;
    }

    private Long toUserId(User user) {
        return user != null ? user.getId() : null;
    }

    private static String fullName(User user) {
        if (user == null) {
            return null;
        }
        String joined = Stream.of(
                        Optional.ofNullable(user.getFirstName()).orElse(null),
                        Optional.ofNullable(user.getLastName()).orElse(null))
                .map(part -> part == null ? "" : part.trim())
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
        return joined.isEmpty() ? null : joined;
    }
}
