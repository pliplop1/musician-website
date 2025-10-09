package com.docker.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.docker.service.CommentService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentApiController {

    private final CommentService commentService;

    public CommentApiController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Approuver un commentaire via AJAX
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveComment(@PathVariable Long id) {
        try {
            commentService.approveComment(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Commentaire approuvé avec succès");
            response.put("commentId", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de l'approbation du commentaire");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Supprimer un commentaire via AJAX
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Commentaire supprimé avec succès");
            response.put("commentId", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression du commentaire");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
