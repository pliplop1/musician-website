package com.docker.security;

import com.docker.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre qui vérifie les tentatives de connexion avant le processus d'authentification
 * Bloque les requêtes si le username ou l'IP sont en état de blocage
 */
@Component
public class LoginAttemptFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptFilter.class);

    private final LoginAttemptService loginAttemptService;

    public LoginAttemptFilter(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        // Vérifier seulement les requêtes POST vers /login
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/login".equals(request.getServletPath())) {
            String username = request.getParameter("username");
            String ipAddress = getClientIP(request);

            // Vérifier si le username est bloqué
            if (username != null && loginAttemptService.isBlocked(username)) {
                logger.warn("🚫 Tentative de connexion bloquée - Username: {}, IP: {}", username, ipAddress);

                // Rediriger avec un message d'erreur
                request.getSession().setAttribute("errorMessage",
                    "Votre compte est temporairement bloqué suite à de multiples tentatives échouées. " +
                    "Veuillez réessayer dans 15 minutes.");
                response.sendRedirect("/login?blocked=true");
                return;
            }

            // Vérifier si l'IP est bloquée
            if (loginAttemptService.isIpBlocked(ipAddress)) {
                logger.warn("🚫 Tentative de connexion bloquée - IP: {}", ipAddress);

                request.getSession().setAttribute("errorMessage",
                    "Trop de tentatives de connexion depuis votre adresse IP. " +
                    "Veuillez réessayer dans 15 minutes.");
                response.sendRedirect("/login?blocked=true");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Récupère l'adresse IP réelle du client (gère les proxies et load balancers)
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
