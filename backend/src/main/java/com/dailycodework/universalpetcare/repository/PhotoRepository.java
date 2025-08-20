package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
