package com.docker.controller.api;

import com.docker.entity.User;
import com.docker.entity.Video;
import com.docker.service.UserService;
import com.docker.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur API REST pour les interactions avec les vidéos
 * - Likes/Unlikes
 * - Tracking des vues
 * - Statistiques
 */
@RestController
@RequestMapping("/api/videos")
public class VideoInteractionController {

    private final VideoService videoService;
    private final UserService userService;

    public VideoInteractionController(VideoService videoService, UserService userService) {
        this.videoService = videoService;
        this.userService = userService;
    }

    /**
     * Ajouter un like à une vidéo
     * POST /api/videos/{id}/like
     */
    @Transactional
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeVideo(@PathVariable Long id, Authentication authentication) {
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

            Video video = videoService.findById(id);

            if (video == null) {
                return ResponseEntity.notFound().build();
            }

            // Ajouter le like via la méthode de l'entité
            video.addLike(user);
            videoService.save(video);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", true);
            response.put("likeCount", video.getLikeCount());
            response.put("message", "Vidéo likée avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors du like: " + e.getMessage()));
        }
    }

    /**
     * Retirer un like d'une vidéo
     * DELETE /api/videos/{id}/like
     */
    @Transactional
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikeVideo(@PathVariable Long id, Authentication authentication) {
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

            Video video = videoService.findById(id);

            if (video == null) {
                return ResponseEntity.notFound().build();
            }

            // Retirer le like via la méthode de l'entité
            video.removeLike(user);
            videoService.save(video);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", false);
            response.put("likeCount", video.getLikeCount());
            response.put("message", "Like retiré avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors du unlike: " + e.getMessage()));
        }
    }

    /**
     * Vérifier si l'utilisateur a liké une vidéo
     * GET /api/videos/{id}/like-status
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}/like-status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable Long id, Authentication authentication) {
        try {
            Video video = videoService.findById(id);
            if (video == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isLiked = false;
            if (authentication != null) {
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                isLiked = video.isLikedBy(user);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("liked", isLiked);
            response.put("likeCount", video.getLikeCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la récupération du statut: " + e.getMessage()));
        }
    }

    /**
     * Incrémenter le compteur de vues d'une vidéo
     * POST /api/videos/{id}/view
     * Peut être appelé sans authentification
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<Map<String, Object>> incrementViewCount(@PathVariable Long id) {
        try {
            Video video = videoService.findById(id);
            if (video == null) {
                return ResponseEntity.notFound().build();
            }

            // Incrémenter les vues via la méthode de l'entité
            video.incrementViewCount();
            videoService.save(video);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("viewCount", video.getViewCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de l'incrémentation des vues: " + e.getMessage()));
        }
    }

    /**
     * Obtenir les statistiques complètes d'une vidéo
     * GET /api/videos/{id}/stats
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getVideoStats(@PathVariable Long id, Authentication authentication) {
        try {
            Video video = videoService.findById(id);
            if (video == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isLiked = false;
            if (authentication != null) {
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                isLiked = video.isLikedBy(user);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("id", video.getId());
            stats.put("title", video.getTitle());
            stats.put("viewCount", video.getViewCount());
            stats.put("likeCount", video.getLikeCount());
            stats.put("isLiked", isLiked);
            stats.put("publishedAt", video.getPublishedAt());
            stats.put("category", video.getCategory());
            stats.put("isFeatured", video.getIsFeatured());

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la récupération des statistiques: " + e.getMessage()));
        }
    }
}
