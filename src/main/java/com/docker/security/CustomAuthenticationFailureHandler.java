package com.docker.security;

import com.docker.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler personnalisé pour les échecs d'authentification
 * Enregistre chaque tentative échouée et gère les blocages anti-brute force
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
        setDefaultFailureUrl("/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        String ipAddress = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");

        // Enregistrer la tentative échouée
        loginAttemptService.recordFailedLogin(username, ipAddress, userAgent, exception.getMessage());

        // Récupérer le nombre de tentatives restantes
        int remainingAttempts = loginAttemptService.getRemainingAttempts(username);

        // Ajouter un attribut de session pour afficher un message personnalisé
        if (remainingAttempts > 0) {
            request.getSession().setAttribute("errorMessage",
                String.format("Identifiants invalides. Il vous reste %d tentative(s).", remainingAttempts));
        } else {
            request.getSession().setAttribute("errorMessage",
                "Compte temporairement bloqué suite à de multiples tentatives échouées. Réessayez dans 15 minutes.");
        }

        logger.warn("Échec de connexion - Username: {}, IP: {}, Tentatives restantes: {}",
                   username, ipAddress, remainingAttempts);

        super.onAuthenticationFailure(request, response, exception);
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
