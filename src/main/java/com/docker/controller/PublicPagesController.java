package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour les pages publiques (accessibles sans authentification)
 * - Politique de confidentialité (RGPD)
 * - Conditions d'utilisation
 * - Mentions légales
 */
@Controller
public class PublicPagesController {

    /**
     * Affiche la page de politique de confidentialité (RGPD)
     * Route publique accessible à tous
     */
    @GetMapping("/privacy-policy")
    public String showPrivacyPolicy() {
        return "privacy-policy";
    }
}
