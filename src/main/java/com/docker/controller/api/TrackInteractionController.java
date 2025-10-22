package com.docker.controller.api;

import com.docker.entity.Track;
import com.docker.entity.User;
import com.docker.service.TrackService;
import com.docker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur API REST pour les interactions avec les musiques
 * - Likes/Unlikes
 * - Tracking des écoutes
 * - Statistiques
 */
@RestController
@RequestMapping("/api/tracks")
public class TrackInteractionController {

    private final TrackService trackService;
    private final UserService userService;

    public TrackInteractionController(TrackService trackService, UserService userService) {
        this.trackService = trackService;
        this.userService = userService;
    }

    /**
     * Ajouter un like à une chanson
     * POST /api/tracks/{id}/like
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeTrack(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentification requise"));
        }

        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            Track track = trackService.findById(id);

            if (track == null) {
                return ResponseEntity.notFound().build();
            }

            // Ajouter le like via la méthode de l'entité
            track.addLike(user);
            trackService.save(track);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", true);
            response.put("likeCount", track.getLikeCount());
            response.put("message", "Chanson likée avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors du like: " + e.getMessage()));
        }
    }

    /**
     * Retirer un like d'une chanson
     * DELETE /api/tracks/{id}/like
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> unlikeTrack(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentification requise"));
        }

        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            Track track = trackService.findById(id);

            if (track == null) {
                return ResponseEntity.notFound().build();
            }

            // Retirer le like via la méthode de l'entité
            track.removeLike(user);
            trackService.save(track);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("liked", false);
            response.put("likeCount", track.getLikeCount());
            response.put("message", "Like retiré avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors du unlike: " + e.getMessage()));
        }
    }

    /**
     * Vérifier si l'utilisateur a liké une chanson
     * GET /api/tracks/{id}/like-status
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}/like-status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable Long id, Authentication authentication) {
        try {
            Track track = trackService.findById(id);
            if (track == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isLiked = false;
            if (authentication != null) {
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                isLiked = track.isLikedBy(user);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("liked", isLiked);
            response.put("likeCount", track.getLikeCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la récupération du statut: " + e.getMessage()));
        }
    }

    /**
     * Incrémenter le compteur d'écoutes d'une chanson
     * POST /api/tracks/{id}/play
     * Peut être appelé sans authentification
     */
    @PostMapping("/{id}/play")
    public ResponseEntity<Map<String, Object>> incrementPlayCount(@PathVariable Long id) {
        try {
            Track track = trackService.findById(id);
            if (track == null) {
                return ResponseEntity.notFound().build();
            }

            // Incrémenter les écoutes via la méthode de l'entité
            track.incrementPlayCount();
            trackService.save(track);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("playCount", track.getPlayCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de l'incrémentation des écoutes: " + e.getMessage()));
        }
    }

    /**
     * Obtenir les statistiques complètes d'une chanson
     * GET /api/tracks/{id}/stats
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getTrackStats(@PathVariable Long id, Authentication authentication) {
        try {
            Track track = trackService.findById(id);
            if (track == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isLiked = false;
            if (authentication != null) {
                String username = authentication.getName();
                User user = userService.findByUsername(username);
                isLiked = track.isLikedBy(user);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("id", track.getId());
            stats.put("title", track.getTitle());
            stats.put("artist", track.getArtist());
            stats.put("album", track.getAlbum());
            stats.put("playCount", track.getPlayCount());
            stats.put("likeCount", track.getLikeCount());
            stats.put("isLiked", isLiked);
            stats.put("duration", track.getFormattedDuration());
            stats.put("genre", track.getGenre());
            stats.put("category", track.getCategory());
            stats.put("publishedAt", track.getPublishedAt());
            stats.put("isFeatured", track.getIsFeatured());

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la récupération des statistiques: " + e.getMessage()));
        }
    }
}
