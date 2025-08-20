package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Photo;
import com.dailycodework.universalpetcare.response.ApiResponse;
import com.dailycodework.universalpetcare.service.photo.IPhotoService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhotoControllerTest {

    @Mock
    private IPhotoService photoService;

    @InjectMocks
    private PhotoController photoController;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSavePhoto_success() throws SQLException, IOException {
        Photo photo = new Photo();
        photo.setId(1L);
        when(photoService.savePhoto(multipartFile, 10L)).thenReturn(photo);

        ResponseEntity<ApiResponse> response = photoController.savePhoto(multipartFile, 10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PHOTO_UPDATE_SUCCESS, response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData());
    }

    @Test
    void testSavePhoto_error() throws SQLException, IOException {
        when(photoService.savePhoto(multipartFile, 10L)).thenThrow(new IOException("IO Error"));

        ResponseEntity<ApiResponse> response = photoController.savePhoto(multipartFile, 10L);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("IO Error", response.getBody().getMessage());
    }

    @Test
    void testGetPhotoById_success() throws SQLException {
        Photo photo = new Photo();
        photo.setId(1L);
        byte[] data = new byte[]{1, 2, 3};

        when(photoService.getPhotoById(1L)).thenReturn(photo);
        when(photoService.getImageData(1L)).thenReturn(data);

        ResponseEntity<ApiResponse> response = photoController.getPhotoById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, response.getBody().getMessage());
        assertArrayEquals(data, (byte[]) response.getBody().getData());
    }

    @Test
    void testGetPhotoById_notFound() throws SQLException {
        when(photoService.getPhotoById(1L)).thenThrow(new ResourceNotFoundException("Not Found"));

        ResponseEntity<ApiResponse> response = photoController.getPhotoById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not Found", response.getBody().getMessage());
    }

    @Test
    void testDeletePhoto_success() throws SQLException {
        Photo photo = new Photo();
        photo.setId(1L);
        when(photoService.getPhotoById(1L)).thenReturn(photo);

        ResponseEntity<ApiResponse> response = photoController.deletePhoto(1L, 10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PHOTO_REMOVE_SUCCESS, response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData());
        verify(photoService).deletePhoto(1L, 10L);
    }

    @Test
    void testDeletePhoto_notFound() throws SQLException {
        when(photoService.getPhotoById(1L)).thenThrow(new ResourceNotFoundException("Not Found"));

        ResponseEntity<ApiResponse> response = photoController.deletePhoto(1L, 10L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not Found", response.getBody().getMessage());
    }

    @Test
    void testUpdatePhoto_success() throws SQLException, IOException {
        Photo photo = new Photo();
        photo.setId(1L);
        Photo updated = new Photo();
        updated.setId(99L);

        when(photoService.getPhotoById(1L)).thenReturn(photo);
        when(photoService.updatePhoto(1L, multipartFile)).thenReturn(updated);

        ResponseEntity<ApiResponse> response = photoController.updatePhoto(1L, multipartFile);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(FeedBackMessage.PHOTO_UPDATE_SUCCESS, response.getBody().getMessage());
        assertEquals(99L, response.getBody().getData());
    }

    @Test
    void testUpdatePhoto_notFound() throws SQLException, IOException {
        when(photoService.getPhotoById(1L)).thenThrow(new ResourceNotFoundException("Not Found"));

        ResponseEntity<ApiResponse> response = photoController.updatePhoto(1L, multipartFile);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not Found", response.getBody().getMessage());
    }
}
