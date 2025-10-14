package com.docker.controller;

import com.docker.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        var pendingComments = commentService.getPendingComments();
        var allComments = commentService.getAllComments();

        System.out.println("=== ADMIN COMMENTS PAGE ===");
        System.out.println("Pending comments: " + pendingComments.size());
        System.out.println("All comments: " + allComments.size());

        pendingComments.forEach(c ->
            System.out.println("  Pending: ID=" + c.getId() + ", user=" + c.getUser().getUsername() + ", type=" + c.getType() + ", targetId=" + c.getTargetId())
        );

        model.addAttribute("pendingComments", pendingComments);
        model.addAttribute("allComments", allComments);
        return "admin/comments";
    }

    /**
     * Approuver un commentaire
     */
    @PostMapping("/approve/{id}")
    public String approveComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            commentService.approveComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Commentaire approuvé avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : Commentaire non trouvé.");
        }
        return "redirect:/admin/comments";
    }

    /**
     * Supprimer un commentaire
     */
    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            commentService.deleteComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Commentaire supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression du commentaire.");
        }
        return "redirect:/admin/comments";
    }

    /**
     * Approuver plusieurs commentaires en masse
     */
    @PostMapping("/bulk-approve")
    public String bulkApproveComments(@RequestParam(name = "commentIds", required = false) List<Long> commentIds, RedirectAttributes redirectAttributes) {
        if (commentIds == null || commentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aucun commentaire sélectionné");
            return "redirect:/admin/comments";
        }

        int count = 0;
        for (Long id : commentIds) {
            commentService.approveComment(id);
            count++;
        }

        redirectAttributes.addFlashAttribute("successMessage", count + " commentaire(s) approuvé(s) avec succès !");
        return "redirect:/admin/comments";
    }

    /**
     * Supprimer plusieurs commentaires en masse
     */
    @PostMapping("/bulk-delete")
    public String bulkDeleteComments(@RequestParam(name = "commentIds", required = false) List<Long> commentIds, RedirectAttributes redirectAttributes) {
        if (commentIds == null || commentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aucun commentaire sélectionné");
            return "redirect:/admin/comments";
        }

        int count = 0;
        for (Long id : commentIds) {
            commentService.deleteComment(id);
            count++;
        }

        redirectAttributes.addFlashAttribute("successMessage", count + " commentaire(s) supprimé(s) avec succès !");
        return "redirect:/admin/comments";
    }
}
