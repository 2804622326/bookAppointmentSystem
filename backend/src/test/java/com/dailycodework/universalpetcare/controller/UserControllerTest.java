package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDto;
import com.dailycodework.universalpetcare.event.RegistrationCompleteEvent;
import com.dailycodework.universalpetcare.exception.AlreadyExistsException;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.password.IChangePasswordService;
import com.dailycodework.universalpetcare.service.user.UserService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private EntityConverter<User, UserDto> entityConverter;

    @Mock
    private IChangePasswordService changePasswordService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_success() {
        RegistrationRequest request = new RegistrationRequest();
        User user = new User();
        UserDto userDto = new UserDto();

        when(userService.register(request)).thenReturn(user);
        when(entityConverter.mapEntityToDto(user, UserDto.class)).thenReturn(userDto);

        ResponseEntity<ApiResponse> response = userController.register(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDto, response.getBody().getData());
        verify(publisher).publishEvent(any(RegistrationCompleteEvent.class));
    }

    @Test
    void testRegister_alreadyExists() {
        RegistrationRequest request = new RegistrationRequest();
        when(userService.register(request)).thenThrow(new AlreadyExistsException("User already exists"));

        ResponseEntity<ApiResponse> response = userController.register(request);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("User already exists", response.getBody().getMessage());
    }

    @Test
    void testFindById_success() throws SQLException {
        UserDto userDto = new UserDto();
        when(userService.getUserWithDetails(1L)).thenReturn(userDto);

        ResponseEntity<ApiResponse> response = userController.findById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDto, response.getBody().getData());
    }

    @Test
    void testFindById_notFound() throws SQLException {
        when(userService.getUserWithDetails(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<ApiResponse> response = userController.findById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void testUpdate_success() {
        UserUpdateRequest request = new UserUpdateRequest();
        User user = new User();
        UserDto userDto = new UserDto();

        when(userService.update(1L, request)).thenReturn(user);
        when(entityConverter.mapEntityToDto(user, UserDto.class)).thenReturn(userDto);

        ResponseEntity<ApiResponse> response = userController.update(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDto, response.getBody().getData());
    }

    @Test
    void testDeleteById_success() {
        ResponseEntity<ApiResponse> response = userController.deleteById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.DELETE_USER_SUCCESS, response.getBody().getMessage());
        verify(userService).delete(1L);
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> users = Arrays.asList(new UserDto(), new UserDto());
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<ApiResponse> response = userController.getAllUsers();

        assertEquals(302, response.getStatusCodeValue());
        assertEquals(users, response.getBody().getData());
    }

    @Test
    void testChangePassword_success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        ResponseEntity<ApiResponse> response = userController.changePassword(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PASSWORD_CHANGE_SUCCESS, response.getBody().getMessage());
        verify(changePasswordService).changePassword(1L, request);
    }

    @Test
    void testChangePassword_illegalArgument() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        doThrow(new IllegalArgumentException("Invalid password format"))
                .when(changePasswordService).changePassword(1L, request);

        ResponseEntity<ApiResponse> response = userController.changePassword(1L, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid password format", response.getBody().getMessage());
    }

    @Test
    void testAggregateUsersByMonthAndType() {
        Map<String, Map<String, Long>> result = new HashMap<>();
        when(userService.aggregateUsersByMonthAndType()).thenReturn(result);

        ResponseEntity<ApiResponse> response = userController.aggregateUsersByMonthAndType();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(result, response.getBody().getData());
    }

    @Test
    void testLockUserAccount() {
        ResponseEntity<ApiResponse> response = userController.lockUserAccount(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.LOCKED_ACCOUNT_SUCCESS, response.getBody().getMessage());
        verify(userService).lockUserAccount(1L);
    }

    @Test
    void testCountFunctions() {
        when(userService.countPatients()).thenReturn(10L);
        when(userService.countVeterinarians()).thenReturn(5L);
        when(userService.countAllUsers()).thenReturn(15L);

        assertEquals(10L, userController.countPatients());
        assertEquals(5L, userController.countVeterinarians());
        assertEquals(15L, userController.countUsers());
    }

    @Test
    void testRegister_internalServerError() {
        RegistrationRequest request = new RegistrationRequest();
        when(userService.register(request)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<ApiResponse> response = userController.register(request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database error", response.getBody().getMessage());
    }

    @Test
    void testUpdate_notFound() {
        UserUpdateRequest request = new UserUpdateRequest();
        when(userService.update(1L, request)).thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<ApiResponse> response = userController.update(1L, request);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void testUpdate_internalServerError() {
        UserUpdateRequest request = new UserUpdateRequest();
        when(userService.update(1L, request)).thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<ApiResponse> response = userController.update(1L, request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Update failed", response.getBody().getMessage());
    }

    @Test
    void testFindById_internalServerError() throws SQLException {
        when(userService.getUserWithDetails(1L)).thenThrow(new RuntimeException("Database connection error"));

        ResponseEntity<ApiResponse> response = userController.findById(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database connection error", response.getBody().getMessage());
    }

    @Test
    void testDeleteById_notFound() {
        doThrow(new ResourceNotFoundException("User not found")).when(userService).delete(1L);

        ResponseEntity<ApiResponse> response = userController.deleteById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void testDeleteById_internalServerError() {
        doThrow(new RuntimeException("Delete failed")).when(userService).delete(1L);

        ResponseEntity<ApiResponse> response = userController.deleteById(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Delete failed", response.getBody().getMessage());
    }

    @Test
    void testChangePassword_notFound() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        doThrow(new ResourceNotFoundException("User not found"))
                .when(changePasswordService).changePassword(1L, request);

        ResponseEntity<ApiResponse> response = userController.changePassword(1L, request);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void testChangePassword_internalServerError() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        doThrow(new RuntimeException("Password service error"))
                .when(changePasswordService).changePassword(1L, request);

        ResponseEntity<ApiResponse> response = userController.changePassword(1L, request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Password service error", response.getBody().getMessage());
    }

    @Test
    void testAggregateUsersByMonthAndType_internalServerError() {
        when(userService.aggregateUsersByMonthAndType()).thenThrow(new RuntimeException("Aggregation failed"));

        ResponseEntity<ApiResponse> response = userController.aggregateUsersByMonthAndType();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Aggregation failed", response.getBody().getMessage());
    }

    @Test
    void testGetAggregatedUsersByEnabledStatus_success() {
        Map<String, Map<String, Long>> result = new HashMap<>();
        result.put("enabled", Map.of("PATIENT", 5L, "VET", 3L));
        when(userService.aggregateUsersByEnabledStatusAndType()).thenReturn(result);

        ResponseEntity<ApiResponse> response = userController.getAggregatedUsersByEnabledStatus();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(result, response.getBody().getData());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, response.getBody().getMessage());
    }

    @Test
    void testGetAggregatedUsersByEnabledStatus_internalServerError() {
        when(userService.aggregateUsersByEnabledStatusAndType()).thenThrow(new RuntimeException("Status aggregation failed"));

        ResponseEntity<ApiResponse> response = userController.getAggregatedUsersByEnabledStatus();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Status aggregation failed", response.getBody().getMessage());
    }

    @Test
    void testLockUserAccount_internalServerError() {
        doThrow(new RuntimeException("Lock failed")).when(userService).lockUserAccount(1L);

        ResponseEntity<ApiResponse> response = userController.lockUserAccount(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Lock failed", response.getBody().getMessage());
    }

    @Test
    void testUnLockUserAccount_success() {
        ResponseEntity<ApiResponse> response = userController.unLockUserAccount(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS, response.getBody().getMessage());
        verify(userService).unLockUserAccount(1L);
    }

    @Test
    void testUnLockUserAccount_internalServerError() {
        doThrow(new RuntimeException("Unlock failed")).when(userService).unLockUserAccount(1L);

        ResponseEntity<ApiResponse> response = userController.unLockUserAccount(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Unlock failed", response.getBody().getMessage());
    }
}
