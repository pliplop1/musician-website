package com.docker.controller;

import com.docker.entity.Comment;
import com.docker.entity.CommentType;
import com.docker.entity.User;
import com.docker.service.CommentService;
import com.docker.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    /**
     * Ajouter un commentaire sur un concert
     */
    @PostMapping("/concert/{concertId}")
    public String addConcertComment(
            @PathVariable Long concertId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(userDetails.getUsername());

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le commentaire ne peut pas être vide.");
            return "redirect:/";
        }

        if (content.length() > 1000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le commentaire ne peut pas dépasser 1000 caractères.");
            return "redirect:/";
        }

        commentService.createComment(content, user, CommentType.CONCERT, concertId);
        redirectAttributes.addFlashAttribute("successMessage", "Votre commentaire a été soumis et sera publié après modération.");

        return "redirect:/";
    }

    /**
     * Ajouter un commentaire sur un article
     */
    @PostMapping("/article/{articleId}")
    public String addArticleComment(
            @PathVariable Long articleId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(userDetails.getUsername());

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le commentaire ne peut pas être vide.");
            return "redirect:/actualites";
        }

        if (content.length() > 1000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le commentaire ne peut pas dépasser 1000 caractères.");
            return "redirect:/actualites";
        }

        commentService.createComment(content, user, CommentType.ARTICLE, articleId);
        redirectAttributes.addFlashAttribute("successMessage", "Votre commentaire a été soumis et sera publié après modération.");

        return "redirect:/actualites";
    }

    /**
     * Ajouter un commentaire sur une vidéo
     */
    @PostMapping("/video/{videoId}")
    public String addVideoComment(
            @PathVariable Long videoId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(userDetails.getUsername());

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le commentaire ne peut pas être vide.");
            return "redirect:/videos";
        }

        if (content.length() > 1000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le commentaire ne peut pas dépasser 1000 caractères.");
            return "redirect:/videos";
        }

        commentService.createComment(content, user, CommentType.VIDEO, videoId);
        redirectAttributes.addFlashAttribute("successMessage", "Votre commentaire a été soumis et sera publié après modération.");

        return "redirect:/videos";
    }
}
