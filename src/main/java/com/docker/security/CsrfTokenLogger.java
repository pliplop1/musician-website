package com.docker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre pour logger les tokens CSRF et détecter les problèmes de validation
 * Utile pour le debugging en développement
 */
@Component
public class CsrfTokenLogger extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CsrfTokenLogger.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Récupérer le token CSRF depuis l'attribut de requête
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            String method = request.getMethod();
            String uri = request.getRequestURI();

            // Logger uniquement pour les méthodes qui nécessitent une validation CSRF
            if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method)) {
                String tokenFromRequest = request.getHeader(csrfToken.getHeaderName());
                if (tokenFromRequest == null) {
                    tokenFromRequest = request.getParameter(csrfToken.getParameterName());
                }

                if (tokenFromRequest == null) {
                    logger.warn("⚠️ CSRF Token manquant - Méthode: {}, URI: {}", method, uri);
                } else {
                    logger.debug("✓ CSRF Token présent - Méthode: {}, URI: {}", method, uri);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
