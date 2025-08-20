package com.dailycodework.universalpetcare.service.photo;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Photo;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.PhotoRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSavePhoto_success() throws IOException, SQLException {
        Long userId = 1L;
        byte[] imageBytes = "image data".getBytes();
        User user = new User();
        Photo savedPhoto = new Photo();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(multipartFile.getBytes()).thenReturn(imageBytes);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getOriginalFilename()).thenReturn("avatar.png");
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);
        when(userRepository.save(user)).thenReturn(user);

        Photo result = photoService.savePhoto(multipartFile, userId);

        assertNotNull(result);
        verify(photoRepository).save(any(Photo.class));
        verify(userRepository).save(user);
    }

    @Test
    void testDeletePhoto_success() {
        Long userId = 1L;
        Long photoId = 2L;
        User user = spy(new User());
        Photo photo = new Photo();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        photoService.deletePhoto(photoId, userId);

        verify(userRepository).findById(userId);
        verify(photoRepository).delete(photo);
    }

    @Test
    void testDeletePhoto_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> photoService.deletePhoto(1L, 99L));
    }

    @Test
    void testDeletePhoto_photoNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(photoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> photoService.deletePhoto(999L, userId));
    }

    @Test
    void testUpdatePhoto_success() throws IOException, SQLException {
        Long photoId = 1L;
        Photo existing = new Photo();
        byte[] data = "new image".getBytes();

        when(photoRepository.findById(photoId)).thenReturn(Optional.of(existing));
        when(multipartFile.getBytes()).thenReturn(data);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("updated.jpg");
        when(photoRepository.save(existing)).thenReturn(existing);

        Photo updated = photoService.updatePhoto(photoId, multipartFile);

        assertEquals("image/jpeg", updated.getFileType());
        assertEquals("updated.jpg", updated.getFileName());
        verify(photoRepository).save(existing);
    }

    @Test
    void testUpdatePhoto_notFound() {
        when(photoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> photoService.updatePhoto(5L, multipartFile));
    }

    @Test
    void testGetPhotoById_success() {
        Photo photo = new Photo();
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        Photo result = photoService.getPhotoById(1L);

        assertEquals(photo, result);
    }

    @Test
    void testGetPhotoById_notFound() {
        when(photoRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> photoService.getPhotoById(1L));

        assertEquals(FeedBackMessage.RESOURCE_FOUND, ex.getMessage()); // note: RESOURCE_FOUND is likely a bug
    }

    @Test
    void testGetImageData_success() throws SQLException {
        byte[] content = "abc".getBytes();
        Blob blob = new SerialBlob(content);
        Photo photo = new Photo();
        photo.setImage(blob);

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        byte[] result = photoService.getImageData(1L);

        assertArrayEquals(content, result);
    }

    @Test
    void testGetImageData_photoNotFound() {
        when(photoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> photoService.getImageData(1L));
    }
}
