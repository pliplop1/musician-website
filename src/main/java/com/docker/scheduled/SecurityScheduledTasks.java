package com.docker.scheduled;

import com.docker.service.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tâches planifiées pour la sécurité
 * - Nettoyage des anciennes tentatives de connexion
 */
@Component
public class SecurityScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(SecurityScheduledTasks.class);

    private final LoginAttemptService loginAttemptService;

    public SecurityScheduledTasks(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Nettoie les tentatives de connexion de plus de 30 jours
     * Exécuté tous les jours à 3h du matin
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupOldLoginAttempts() {
        logger.info("Début du nettoyage automatique des anciennes tentatives de connexion");

        try {
            loginAttemptService.cleanupOldAttempts();
            logger.info("Nettoyage des tentatives de connexion terminé avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors du nettoyage des tentatives de connexion", e);
        }
    }
}
