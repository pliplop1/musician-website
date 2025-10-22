package com.docker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre pour ajouter l'en-tête Content-Security-Policy (CSP)
 *
 * CSP protège contre les attaques XSS et l'injection de code malveillant
 * en contrôlant quelles ressources peuvent être chargées par le navigateur.
 */
@Component
public class ContentSecurityPolicyFilter extends OncePerRequestFilter {

    /**
     * Content Security Policy configurée pour le site de musicien
     *
     * Directives:
     * - default-src 'self': Par défaut, seules les ressources du même domaine sont autorisées
     * - script-src: Scripts autorisés (self + inline pour Vue.js et autres frameworks)
     * - style-src: Styles autorisés (self + inline pour les styles dynamiques)
     * - img-src: Images autorisées (self + data: pour les images base64)
     * - font-src: Polices autorisées
     * - connect-src: Connexions AJAX autorisées (self + localhost pour le dev)
     * - media-src: Médias audio/vidéo autorisés
     * - frame-ancestors: Empêche l'iframe du site (clickjacking)
     * - base-uri: Limite les URL de base
     * - form-action: Limite les destinations des formulaires
     */
    private static final String CSP_POLICY =
        "default-src 'self'; " +
        "script-src 'self' 'unsafe-inline' 'unsafe-eval' http://localhost:5173 https://cdn.jsdelivr.net; " +
        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdnjs.cloudflare.com; " +
        "img-src 'self' data: https:; " +
        "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com; " +
        "connect-src 'self' http://localhost:5173 http://localhost:8106; " +
        "media-src 'self'; " +
        // Autoriser l'intégration d'iframes externes spécifiques (YouTube, Spotify, SoundCloud)
        "frame-src 'self' https://www.youtube.com https://www.youtube-nocookie.com https://open.spotify.com https://w.soundcloud.com; " +
        "frame-ancestors 'none'; " +
        "base-uri 'self'; " +
        "form-action 'self'";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Ajouter l'en-tête CSP à toutes les réponses
        response.setHeader("Content-Security-Policy", CSP_POLICY);

        // Mode rapport uniquement (pour tester sans bloquer)
        // Décommenter pour passer en mode report-only pendant les tests
        // response.setHeader("Content-Security-Policy-Report-Only", CSP_POLICY);

        filterChain.doFilter(request, response);
    }
}
