package com.docker.controller;

import com.docker.entity.SocialLink;
import com.docker.service.SocialLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/social-links")
public class AdminSocialLinkController {

    @Autowired
    private SocialLinkService socialLinkService;

    /**
     * Affiche la page de gestion des réseaux sociaux.
     */
    @GetMapping
    public String showSocialLinksPage(Model model) {
        model.addAttribute("socialLinks", socialLinkService.getAllSocialLinks());
        return "admin/social-links";
    }

    /**
     * Met à jour un lien social.
     */
    @PostMapping("/update/{id}")
    public String updateSocialLink(@PathVariable Long id,
                                   @RequestParam("url") String url,
                                   @RequestParam(value = "enabled", required = false) Boolean enabled,
                                   RedirectAttributes redirectAttributes) {
        try {
            socialLinkService.updateSocialLink(id, url, enabled);
            redirectAttributes.addFlashAttribute("successMessage", "Réseau social mis à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/social-links";
    }
}
