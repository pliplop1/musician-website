package com.docker.service;

import com.docker.entity.Photo;
import com.docker.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour PhotoService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PhotoServiceTest {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    void testGetAllPhotos_ReturnsAllPhotos() {
        // Given
        createAndSavePhoto("photo1.jpg", "Test Photo 1");
        createAndSavePhoto("photo2.jpg", "Test Photo 2");

        // When
        List<Photo> photos = photoService.getAllPhotos();

        // Then
        assertTrue(photos.size() >= 2);
    }

    @Test
    void testFindPhotoById_ExistingPhoto_ReturnsPhoto() {
        // Given
        Photo photo = createAndSavePhoto("test.jpg", "Test Photo");

        // When
        Optional<Photo> found = photoRepository.findById(photo.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("test.jpg", found.get().getFilename());
    }

    @Test
    void testFindPhotoById_NonExistingPhoto_ReturnsEmpty() {
        // When
        Optional<Photo> found = photoRepository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testDeletePhoto_ExistingPhoto_Success() throws Exception {
        // Given
        Photo photo = createAndSavePhoto("delete.jpg", "Delete Me");
        Long photoId = photo.getId();

        // When
        photoService.deletePhoto(photoId);

        // Then
        assertFalse(photoRepository.findById(photoId).isPresent());
    }

    /**
     * Helper method to create and save a photo
     */
    private Photo createAndSavePhoto(String filename, String description) {
        Photo photo = new Photo();
        photo.setFilename(filename);
        return photoRepository.save(photo);
    }
}
