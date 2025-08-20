package com.dailycodework.universalpetcare.repository;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);


    @Modifying
    @Transactional
    @Query("UPDATE User u " +
            "SET u.firstName = :firstName, " +
            "    u.lastName  = :lastName, " +
            "    u.gender    = :gender, " +
            "    u.phoneNumber = :phoneNumber " +
            "WHERE u.id = :userId")
    int updateUser(@Param("userId")      Long   userId,
                   @Param("firstName")   String firstName,
                   @Param("lastName")    String lastName,
                   @Param("gender")      String gender,
                   @Param("phoneNumber") String phoneNumber);

    /**
     * 根据 userType 查找所有用户（如果 Veterinarian 继承自 User，可按需强转）
     */
    List<Veterinarian> findAllByUserType(String userType);

    long countByUserType(String type);


    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isEnabled = :enabled WHERE u.id = :userId")
    void updateUserEnabledStatus(@Param("userId") Long    userId,
                                 @Param("enabled") boolean enabled);
}