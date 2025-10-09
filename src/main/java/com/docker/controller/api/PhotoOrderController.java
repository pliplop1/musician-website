package com.docker.controller.api;

import com.docker.entity.Photo;
import com.docker.repository.PhotoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@PreAuthorize("hasRole('ADMIN')")
public class PhotoOrderController {

    private final PhotoRepository photoRepository;

    public PhotoOrderController(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @PostMapping("/reorder")
    public ResponseEntity<Map<String, String>> reorderPhotos(@RequestBody List<Long> photoIds) {
        try {
            for (int i = 0; i < photoIds.size(); i++) {
                Long photoId = photoIds.get(i);
                Photo photo = photoRepository.findById(photoId).orElse(null);
                if (photo != null) {
                    photo.setDisplayOrder(i);
                    photoRepository.save(photo);
                }
            }
            return ResponseEntity.ok(Map.of("message", "Ordre mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors de la mise à jour de l'ordre"));
        }
    }
}
