package com.dailycodework.universalpetcare.dto;

import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityConverterReviewDtoTest {


    private EntityConverter<Review, ReviewDto> entityConverter;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();

        // 自定义 converter 合并 firstName + lastName
        Converter<User, String> fullNameConverter = ctx ->
                ctx.getSource() == null ? null :
                        ctx.getSource().getFirstName() + " " + ctx.getSource().getLastName();

        // 为 Review → ReviewDto 配置特殊映射
        TypeMap<Review, ReviewDto> typeMap = modelMapper.createTypeMap(Review.class, ReviewDto.class);
        typeMap.addMappings(mapper -> {
            mapper.using(fullNameConverter).map(Review::getPatient, ReviewDto::setPatientName);
            mapper.using(fullNameConverter).map(Review::getVeterinarian, ReviewDto::setVeterinarianName);
        });

        entityConverter = new EntityConverter<>(modelMapper);
    }

    @Test
    void testMapReviewToReviewDto() {
        User patient = new User();
        patient.setFirstName("John");
        patient.setLastName("Doe");

        User vet = new User();
        vet.setFirstName("Dr.");
        vet.setLastName("Smith");

        Review review = new Review();
        review.setId(1L);
        review.setStars(5);
        review.setFeedback("Excellent service!");
        review.setPatient(patient);
        review.setVeterinarian(vet);

        ReviewDto dto = entityConverter.mapEntityToDto(review, ReviewDto.class);

        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getStars());
        assertEquals("Excellent service!", dto.getFeedback());
        assertEquals("John Doe", dto.getPatientName());
        assertEquals("Dr. Smith", dto.getVeterinarianName());
    }
}