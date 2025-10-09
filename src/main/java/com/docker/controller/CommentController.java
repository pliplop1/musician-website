package com.docker.controller;

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

    private static final int MAX_COMMENT_LENGTH = 1000;
    private static final String SUCCESS_MESSAGE = "💬 Commentaire envoyé ! Il sera visible après modération par l'administrateur.";
    private static final String EMPTY_ERROR = "Le commentaire ne peut pas être vide.";
    private static final String LENGTH_ERROR = "Le commentaire ne peut pas dépasser " + MAX_COMMENT_LENGTH + " caractères.";

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/concert/{concertId}")
    public ResponseEntity<Map<String, Object>> addConcertComment(
            @PathVariable Long concertId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        return addComment(content, userDetails, CommentType.CONCERT, concertId);
    }

    @PostMapping("/article/{articleId}")
    public ResponseEntity<Map<String, Object>> addArticleComment(
            @PathVariable Long articleId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        return addComment(content, userDetails, CommentType.ARTICLE, articleId);
    }

    @PostMapping("/video/{videoId}")
    public ResponseEntity<Map<String, Object>> addVideoComment(
            @PathVariable Long videoId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        return addComment(content, userDetails, CommentType.VIDEO, videoId);
    }

    private ResponseEntity<Map<String, Object>> addComment(
            String content,
            UserDetails userDetails,
            CommentType commentType,
            Long entityId) {

        Map<String, Object> response = new HashMap<>();

        try {
            User user = userService.findByUsername(userDetails.getUsername());

            String validationError = validateContent(content);
            if (validationError != null) {
                return buildErrorResponse(validationError, HttpStatus.BAD_REQUEST);
            }

            commentService.createComment(content, user, commentType, entityId);

            response.put("success", true);
            response.put("message", SUCCESS_MESSAGE);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (Exception e) {
            return buildErrorResponse("Une erreur est survenue : " + e.getMessage(),
                                     HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return EMPTY_ERROR;
        }
        if (content.length() > MAX_COMMENT_LENGTH) {
            return LENGTH_ERROR;
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
