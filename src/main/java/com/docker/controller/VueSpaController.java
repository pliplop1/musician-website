package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour servir le SPA Vue.js en production
 * Redirige toutes les routes non-API vers index.html du Vue.js
 *
 * ⚠️ DÉSACTIVÉ TEMPORAIREMENT - Cause une boucle infinie de redirections
 * En mode développement, utilisez Vue.js sur localhost:5173 séparément
 */
// @Controller - COMMENTÉ POUR ÉVITER LA BOUCLE INFINIE
public class VueSpaController {

    /**
     * Route principale pour le SPA Vue.js
     * Accessible via /public ou /
     */
    @GetMapping({"/public", "/public/**"})
    public String forwardToVue() {
        return "forward:/vue/index.html";
    }

    /**
     * Fallback pour toutes les autres routes qui ne correspondent pas à l'admin ou l'API
     * Ceci permet au Vue Router de gérer la navigation côté client
     */
    @GetMapping(value = "/{path:^(?!admin|api|uploaded-photos|uploaded-tracks|css|js|images|videos).*$}/**")
    public String fallbackToVue() {
        return "forward:/vue/index.html";
    }
}
