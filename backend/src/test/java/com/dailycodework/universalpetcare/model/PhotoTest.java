package com.dailycodework.universalpetcare.model;

import org.junit.jupiter.api.Test;

import java.sql.Blob;

import static org.junit.jupiter.api.Assertions.*;

class PhotoTest {

    @Test
    void testPhotoFields() {
        Photo photo = new Photo();
        Blob blob = null;

        photo.setId(1L);
        photo.setFileType("image/png");
        photo.setFileName("profile.png");
        photo.setImage(blob);

        assertEquals(1L, photo.getId());
        assertEquals("image/png", photo.getFileType());
        assertEquals("profile.png", photo.getFileName());
        assertNull(photo.getImage());
    }

    @Test
    void testAllArgsConstructor() {
        Blob blob = null;
        Photo photo = new Photo(2L, "image/jpeg", "avatar.jpg", blob);

        assertEquals(2L, photo.getId());
        assertEquals("image/jpeg", photo.getFileType());
        assertEquals("avatar.jpg", photo.getFileName());
        assertNull(photo.getImage());
    }
}