package com.docker.controller.api;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API REST pour obtenir le token CSRF
 * Utilisé par le frontend Vue.js pour sécuriser les requêtes POST/PUT/DELETE
 */
@RestController
@RequestMapping("/api/public")
public class CsrfController {

    /**
     * Retourne le token CSRF pour le client
     * Ce endpoint est public et accessible sans authentification
     */
    @GetMapping("/csrf")
    public Map<String, String> getCsrfToken(CsrfToken csrfToken) {
        Map<String, String> response = new HashMap<>();
        if (csrfToken != null) {
            response.put("token", csrfToken.getToken());
            response.put("headerName", csrfToken.getHeaderName());
            response.put("parameterName", csrfToken.getParameterName());
        }
        return response;
    }
}
