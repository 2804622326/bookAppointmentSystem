package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.model.Role;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.RoleRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Role patientRole;
    private Role vetRole;

    @BeforeEach
    void setUp() {
        // Clean database
        userRepository.deleteAll();
        roleRepository.deleteAll();
        
        // Create test roles
        patientRole = roleRepository.save(new Role("ROLE_PATIENT"));
        vetRole = roleRepository.save(new Role("ROLE_VET"));
    }

    @Test
    void registerUser_withValidData_shouldCreateUser() throws Exception {
        Map<String, Object> registrationRequest = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "gender", "Male",
            "phoneNumber", "1234567890",
            "email", "john.doe@example.com",
            "password", "password123",
            "userType", "PATIENT"
        );

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.CREATE_USER_SUCCESS))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.userType").value("PATIENT"));
    }

    @Test
    void registerUser_withDuplicateEmail_shouldReturnConflict() throws Exception {
        // Create existing user
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setPassword("password");
        existingUser.setUserType("PATIENT");
        existingUser.setEnabled(true);
        existingUser.setRoles(new HashSet<>(Set.of(patientRole)));
        userRepository.save(existingUser);

        Map<String, Object> registrationRequest = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "gender", "Male", 
            "phoneNumber", "1234567890",
            "email", "existing@example.com", // Same email
            "password", "password123",
            "userType", "PATIENT"
        );

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void updateUser_withValidData_shouldUpdateUser() throws Exception {
        // Create user to update
        User user = new User();
        user.setEmail("update@example.com");
        user.setFirstName("Old");
        user.setLastName("Name");
        user.setPassword("password");
        user.setUserType("PATIENT");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Set.of(patientRole)));
        User savedUser = userRepository.save(user);

        Map<String, Object> updateRequest = Map.of(
            "firstName", "Updated",
            "lastName", "User",
            "gender", "Female",
            "phoneNumber", "9876543210",
            "specialization", "General"
        );

        mockMvc.perform(put("/api/v1/users/user/" + savedUser.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_UPDATE_SUCCESS))
                .andExpect(jsonPath("$.data.firstName").value("Updated"))
                .andExpect(jsonPath("$.data.lastName").value("User"));
    }

    @Test
    void updateUser_withInvalidId_shouldReturnNotFound() throws Exception {
        Map<String, Object> updateRequest = Map.of(
            "firstName", "Updated",
            "lastName", "User"
        );

        mockMvc.perform(put("/api/v1/users/user/999/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void getUserById_withValidId_shouldReturnUser() throws Exception {
        // Create test user
        User user = new User();
        user.setEmail("get@example.com");
        user.setFirstName("Get");
        user.setLastName("User");
        user.setPassword("password");
        user.setUserType("VET");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Set.of(vetRole)));
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/user/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_FOUND))
                .andExpect(jsonPath("$.data.email").value("get@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("Get"))
                .andExpect(jsonPath("$.data.userType").value("VET"));
    }

    @Test
    void getUserById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void deleteUser_withValidId_shouldDeleteUser() throws Exception {
        // Create user to delete
        User user = new User();
        user.setEmail("delete@example.com");
        user.setFirstName("Delete");
        user.setLastName("User");
        user.setPassword("password");
        user.setUserType("PATIENT");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Set.of(patientRole)));
        User savedUser = userRepository.save(user);

        mockMvc.perform(delete("/api/v1/users/user/" + savedUser.getId() + "/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.DELETE_USER_SUCCESS));

        // Verify user is deleted
        mockMvc.perform(get("/api/v1/users/user/" + savedUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user/999/delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void getAllUsers_shouldReturnUserList() throws Exception {
        // Create test users
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setPassword("password");
        user1.setUserType("PATIENT");
        user1.setEnabled(true);
        user1.setRoles(new HashSet<>(Set.of(patientRole)));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setPassword("password");
        user2.setUserType("VET");
        user2.setEnabled(true);
        user2.setRoles(new HashSet<>(Set.of(vetRole)));

        userRepository.save(user1);
        userRepository.save(user2);

        mockMvc.perform(get("/api/v1/users/all-users"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_FOUND))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].email", containsInAnyOrder("user1@example.com", "user2@example.com")));
    }

    @Test
    void lockUserAccount_shouldDisableUser() throws Exception {
        // Create user to lock
        User user = new User();
        user.setEmail("lock@example.com");
        user.setFirstName("Lock");
        user.setLastName("User");
        user.setPassword("password");
        user.setUserType("PATIENT");
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Set.of(patientRole)));
        User savedUser = userRepository.save(user);

        mockMvc.perform(put("/api/v1/users/account/" + savedUser.getId() + "/lock-user-account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.LOCKED_ACCOUNT_SUCCESS));
    }

    @Test 
    void unlockUserAccount_shouldEnableUser() throws Exception {
        // Create disabled user 
        User user = new User();
        user.setEmail("unlock@example.com");
        user.setFirstName("Unlock");
        user.setLastName("User");
        user.setPassword("password");
        user.setUserType("PATIENT");
        user.setEnabled(false); // Initially disabled
        user.setRoles(new HashSet<>(Set.of(patientRole)));
        User savedUser = userRepository.save(user);

        mockMvc.perform(put("/api/v1/users/account/" + savedUser.getId() + "/unLock-user-account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS));
    }
}