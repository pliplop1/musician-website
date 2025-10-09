package com.docker.controller;

import com.docker.entity.Comment;
import com.docker.entity.CommentType;
import com.docker.entity.User;
import com.docker.service.CommentService;
import com.docker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    /**
     * Ajouter un commentaire sur un concert (AJAX JSON)
     */
    @PostMapping("/concert/{concertId}")
    public ResponseEntity<Map<String, Object>> addConcertComment(
            @PathVariable Long concertId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.findByUsername(userDetails.getUsername());

            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le commentaire ne peut pas être vide.");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            if (content.length() > 1000) {
                response.put("success", false);
                response.put("message", "Le commentaire ne peut pas dépasser 1000 caractères.");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            commentService.createComment(content, user, CommentType.CONCERT, concertId);

            response.put("success", true);
            response.put("message", "💬 Commentaire envoyé ! Il sera visible après modération par l'administrateur.");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Une erreur est survenue : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    /**
     * Ajouter un commentaire sur un article (AJAX JSON)
     */
    @PostMapping("/article/{articleId}")
    public ResponseEntity<Map<String, Object>> addArticleComment(
            @PathVariable Long articleId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.findByUsername(userDetails.getUsername());

            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le commentaire ne peut pas être vide.");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            if (content.length() > 1000) {
                response.put("success", false);
                response.put("message", "Le commentaire ne peut pas dépasser 1000 caractères.");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            commentService.createComment(content, user, CommentType.ARTICLE, articleId);

            response.put("success", true);
            response.put("message", "💬 Commentaire envoyé ! Il sera visible après modération par l'administrateur.");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Une erreur est survenue : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    /**
     * Ajouter un commentaire sur une vidéo (AJAX JSON)
     */
    @PostMapping("/video/{videoId}")
    public ResponseEntity<Map<String, Object>> addVideoComment(
            @PathVariable Long videoId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.findByUsername(userDetails.getUsername());

            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le commentaire ne peut pas être vide.");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            if (content.length() > 1000) {
                response.put("success", false);
                response.put("message", "Le commentaire ne peut pas dépasser 1000 caractères.");
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            commentService.createComment(content, user, CommentType.VIDEO, videoId);

            response.put("success", true);
            response.put("message", "💬 Commentaire envoyé ! Il sera visible après modération par l'administrateur.");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Une erreur est survenue : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }
}
