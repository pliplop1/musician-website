package com.docker.controller.api;

import com.docker.entity.Playlist;
import com.docker.service.PlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur API REST pour les playlists
 * Gestion CRUD des playlists de musiques
 */
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    /**
     * Récupérer toutes les playlists publiques
     * GET /api/playlists
     */
    @GetMapping
    public ResponseEntity<List<Playlist>> getPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getPublicPlaylists());
    }

    /**
     * Récupérer les playlists mises en avant
     * GET /api/playlists/featured
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Playlist>> getFeaturedPlaylists() {
        return ResponseEntity.ok(playlistService.getFeaturedPlaylists());
    }

    /**
     * Récupérer une playlist par son ID
     * GET /api/playlists/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.findById(id);
        if (playlist == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(playlist);
    }

    /**
     * Créer une nouvelle playlist (admin uniquement)
     * POST /api/playlists
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist playlist) {
        try {
            Playlist saved = playlistService.save(playlist);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Mettre à jour une playlist (admin uniquement)
     * PUT /api/playlists/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id, @RequestBody Playlist playlist) {
        try {
            Playlist existing = playlistService.findById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }
            playlist.setId(id);
            Playlist updated = playlistService.save(playlist);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Ajouter une musique à une playlist (admin uniquement)
     * POST /api/playlists/{id}/tracks/{trackId}
     */
    @PostMapping("/{id}/tracks/{trackId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addTrackToPlaylist(
            @PathVariable Long id,
            @PathVariable Long trackId) {
        try {
            playlistService.addTrackToPlaylist(id, trackId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Musique ajoutée à la playlist");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de l'ajout: " + e.getMessage()));
        }
    }

    /**
     * Retirer une musique d'une playlist (admin uniquement)
     * DELETE /api/playlists/{id}/tracks/{trackId}
     */
    @DeleteMapping("/{id}/tracks/{trackId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> removeTrackFromPlaylist(
            @PathVariable Long id,
            @PathVariable Long trackId) {
        try {
            playlistService.removeTrackFromPlaylist(id, trackId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Musique retirée de la playlist");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    /**
     * Supprimer une playlist (admin uniquement)
     * DELETE /api/playlists/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePlaylist(@PathVariable Long id) {
        try {
            playlistService.deletePlaylist(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Playlist supprimée"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}
