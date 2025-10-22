package com.docker.controller.api;

import com.docker.entity.Photo;
import com.docker.entity.User;
import com.docker.service.PhotoService;
import com.docker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur API REST pour les interactions avec les photos
 * - Likes/Unlikes
 * - Tracking des vues
 * - Statistiques
 */
@RestController
@RequestMapping("/api/photos")
public class PhotoInteractionController {

    private final PhotoService photoService;
    private final UserService userService;

    public PhotoInteractionController(PhotoService photoService, UserService userService) {
        this.photoService = photoService;
        this.userService = userService;
    }

    /**
     * Ajouter un like à une photo
     * POST /api/photos/{id}/like
     */
    @Transactional
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likePhoto(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentification requise"));
        }

        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user == null) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Utilisateur non trouvé en base de données"));
            }

            Photo photo = photoService.findById(id);

            if (photo == null) {
                return ResponseEntity.notFound().build();
            }

            // Ajouter le like via la méthode de l'entité
            photo.addLike(user);
            photoService.save(photo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", true);
            response.put("likeCount", photo.getLikeCount());
            response.put("message", "Photo likée avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors du like: " + e.getMessage()));
        }
    }

    /**
     * Retirer un like d'une photo
     * DELETE /api/photos/{id}/like
     */
    @Transactional
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikePhoto(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentification requise"));
        }

        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user == null) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Utilisateur non trouvé en base de données"));
            }

            Photo photo = photoService.findById(id);

            if (photo == null) {
                return ResponseEntity.notFound().build();
            }

            // Retirer le like via la méthode de l'entité
            photo.removeLike(user);
            photoService.save(photo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", false);
            response.put("likeCount", photo.getLikeCount());
            response.put("message", "Like retiré avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors du unlike: " + e.getMessage()));
        }
    }

    /**
     * Vérifier si l'utilisateur a liké une photo
     * GET /api/photos/{id}/like-status
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}/like-status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable Long id, Authentication authentication) {
        try {
            Photo photo = photoService.findById(id);
            if (photo == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isLiked = false;
            if (authentication != null) {
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                isLiked = photo.isLikedBy(user);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("liked", isLiked);
            response.put("likeCount", photo.getLikeCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la récupération du statut: " + e.getMessage()));
        }
    }

    /**
     * Incrémenter le compteur de vues d'une photo
     * POST /api/photos/{id}/view
     * Peut être appelé sans authentification
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<Map<String, Object>> incrementViewCount(@PathVariable Long id) {
        try {
            Photo photo = photoService.findById(id);
            if (photo == null) {
                return ResponseEntity.notFound().build();
            }

            // Incrémenter les vues via la méthode de l'entité
            photo.incrementViewCount();
            photoService.save(photo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("viewCount", photo.getViewCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de l'incrémentation des vues: " + e.getMessage()));
        }
    }

    /**
     * Obtenir les statistiques complètes d'une photo
     * GET /api/photos/{id}/stats
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getPhotoStats(@PathVariable Long id, Authentication authentication) {
        try {
            Photo photo = photoService.findById(id);
            if (photo == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isLiked = false;
            if (authentication != null) {
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                isLiked = photo.isLikedBy(user);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("id", photo.getId());
            stats.put("title", photo.getTitle());
            stats.put("filename", photo.getFilename());
            stats.put("viewCount", photo.getViewCount());
            stats.put("likeCount", photo.getLikeCount());
            stats.put("isLiked", isLiked);
            stats.put("photographer", photo.getPhotographer());
            stats.put("location", photo.getLocation());
            stats.put("takenAt", photo.getTakenAt());
            stats.put("category", photo.getCategory());
            stats.put("isFeatured", photo.getIsFeatured());
            stats.put("fileSize", photo.getFormattedFileSize());
            stats.put("dimensions", photo.getWidth() + "x" + photo.getHeight());
            stats.put("aspectRatio", photo.getAspectRatio());

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la récupération des statistiques: " + e.getMessage()));
        }
    }
}
