package com.docker.controller;

import com.docker.dto.SecurityStatsDTO;
import com.docker.service.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur pour la gestion de la sécurité et du monitoring des connexions
 * Réservé aux administrateurs
 */
@Controller
@RequestMapping("/admin/security")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSecurityController {

    private static final Logger logger = LoggerFactory.getLogger(AdminSecurityController.class);

    private final LoginAttemptService loginAttemptService;

    public AdminSecurityController(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Affiche le dashboard de sécurité avec statistiques et monitoring
     *
     * @param model Modèle Spring MVC
     * @return Vue du dashboard de sécurité
     */
    @GetMapping
    public String showSecurityDashboard(Model model, RedirectAttributes redirectAttributes) {
        logger.info("Accès au dashboard de sécurité");

        try {
            // Récupère toutes les statistiques de sécurité
            SecurityStatsDTO stats = loginAttemptService.getSecurityStats();

            // Ajoute les données au modèle
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "Dashboard de Sécurité");

            logger.info("Dashboard chargé - Total tentatives: {}, Comptes bloqués: {}",
                       stats.getTotalAttempts(), stats.getBlockedAccounts());

            return "admin/security-dashboard";

        } catch (Exception e) {
            logger.error("Erreur lors du chargement du dashboard de sécurité", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du chargement des statistiques de sécurité.");
            return "redirect:/admin/dashboard";
        }
    }
}
