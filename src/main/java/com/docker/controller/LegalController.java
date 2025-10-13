package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller pour les pages légales (RGPD, mentions légales, cookies)
 * Ces pages sont obligatoires pour la conformité légale
 */
@Controller
public class LegalController {

    /**
     * Page de politique de confidentialité (RGPD)
     * Détaille la collecte et l'utilisation des données personnelles
     */
    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "legal/privacy-policy";
    }

    /**
     * Page des mentions légales
     * Informations légales obligatoires sur l'éditeur du site
     */
    @GetMapping("/mentions-legales")
    public String mentionsLegales() {
        return "legal/mentions-legales";
    }

    /**
     * Page de politique des cookies
     * Explique l'utilisation des cookies et du localStorage
     */
    @GetMapping("/cookies")
    public String cookies() {
        return "legal/cookies";
    }
}
