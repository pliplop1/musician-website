package com.docker.security;

import com.docker.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Handler personnalisé pour les authentifications réussies
 * Enregistre chaque connexion réussie et redirige selon le rôle
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationSuccessHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        String ipAddress = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");

        // Enregistrer la connexion réussie
        loginAttemptService.recordSuccessfulLogin(username, ipAddress, userAgent);

        logger.info("✅ Connexion réussie - Username: {}, IP: {}", username, ipAddress);

        // Déterminer la redirection selon le rôle
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        String targetUrl;
        if (roles.contains("ROLE_ADMIN")) {
            targetUrl = "/admin/dashboard";
            logger.info("Redirection vers le tableau de bord admin pour {}", username);
        } else if (roles.contains("ROLE_USER")) {
            targetUrl = "/user/profile";
            logger.info("Redirection vers le profil utilisateur pour {}", username);
        } else {
            targetUrl = "/";
            logger.info("Redirection vers l'accueil pour {} (aucun rôle spécifique)", username);
        }

        setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
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
