package com.docker.controller;

import com.docker.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Page de modération des commentaires
     */
    @GetMapping
    public String showCommentsManagement(Model model) {
        model.addAttribute("pendingComments", commentService.getPendingComments());
        model.addAttribute("allComments", commentService.getAllComments());
        return "admin/comments";
    }

    /**
     * Approuver un commentaire
     */
    @PostMapping("/approve/{id}")
    public String approveComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.approveComment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Commentaire approuvé avec succès !");
        return "redirect:/admin/comments";
    }

    /**
     * Supprimer un commentaire
     */
    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.deleteComment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Commentaire supprimé avec succès !");
        return "redirect:/admin/comments";
    }
}
