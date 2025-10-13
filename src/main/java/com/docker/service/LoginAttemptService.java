package com.docker.service;

import com.docker.entity.LoginAttempt;
import com.docker.repository.LoginAttemptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour gérer la protection anti-brute force des connexions
 *
 * Règles de sécurité :
 * - Maximum 5 tentatives échouées par username dans une fenêtre de 15 minutes
 * - Maximum 10 tentatives échouées par IP dans une fenêtre de 15 minutes
 * - Blocage temporaire de 15 minutes après dépassement du seuil
 */
@Service
@Transactional
public class LoginAttemptService {

    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    // Configuration de la protection anti-brute force
    private static final int MAX_ATTEMPTS_PER_USERNAME = 5;
    private static final int MAX_ATTEMPTS_PER_IP = 10;
    private static final int BLOCK_DURATION_MINUTES = 15;
    private static final int ATTEMPT_WINDOW_MINUTES = 15;

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    /**
     * Enregistre une tentative de connexion réussie
     *
     * @param username Nom d'utilisateur
     * @param ipAddress Adresse IP
     * @param userAgent User-Agent du navigateur
     */
    public void recordSuccessfulLogin(String username, String ipAddress, String userAgent) {
        LoginAttempt attempt = new LoginAttempt(username, ipAddress, true);
        attempt.setUserAgent(userAgent);
        loginAttemptRepository.save(attempt);

        logger.info("Connexion réussie - Username: {}, IP: {}", username, ipAddress);
    }

    /**
     * Enregistre une tentative de connexion échouée
     *
     * @param username Nom d'utilisateur
     * @param ipAddress Adresse IP
     * @param userAgent User-Agent du navigateur
     * @param failureReason Raison de l'échec
     */
    public void recordFailedLogin(String username, String ipAddress, String userAgent, String failureReason) {
        LoginAttempt attempt = new LoginAttempt(username, ipAddress, false, failureReason);
        attempt.setUserAgent(userAgent);
        loginAttemptRepository.save(attempt);

        long failedCount = getFailedAttemptsByUsername(username);
        logger.warn("Tentative de connexion échouée - Username: {}, IP: {}, Tentatives: {}/{}",
                    username, ipAddress, failedCount, MAX_ATTEMPTS_PER_USERNAME);

        if (failedCount >= MAX_ATTEMPTS_PER_USERNAME) {
            logger.error("⚠️ ALERTE SÉCURITÉ - Compte bloqué temporairement : {}", username);
        }
    }

    /**
     * Vérifie si un username est bloqué (trop de tentatives échouées)
     *
     * @param username Nom d'utilisateur
     * @return true si bloqué, false sinon
     */
    public boolean isBlocked(String username) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(BLOCK_DURATION_MINUTES);
        long failedAttempts = loginAttemptRepository.countFailedAttemptsByUsernameSince(username, cutoffTime);

        boolean blocked = failedAttempts >= MAX_ATTEMPTS_PER_USERNAME;

        if (blocked) {
            logger.warn("Tentative de connexion bloquée pour l'utilisateur : {} ({} tentatives échouées)",
                       username, failedAttempts);
        }

        return blocked;
    }

    /**
     * Vérifie si une IP est bloquée (trop de tentatives échouées)
     *
     * @param ipAddress Adresse IP
     * @return true si bloquée, false sinon
     */
    public boolean isIpBlocked(String ipAddress) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(BLOCK_DURATION_MINUTES);
        long failedAttempts = loginAttemptRepository.countFailedAttemptsByIpSince(ipAddress, cutoffTime);

        boolean blocked = failedAttempts >= MAX_ATTEMPTS_PER_IP;

        if (blocked) {
            logger.warn("Tentative de connexion bloquée pour l'IP : {} ({} tentatives échouées)",
                       ipAddress, failedAttempts);
        }

        return blocked;
    }

    /**
     * Récupère le nombre de tentatives échouées récentes pour un username
     *
     * @param username Nom d'utilisateur
     * @return Nombre de tentatives échouées
     */
    public long getFailedAttemptsByUsername(String username) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(ATTEMPT_WINDOW_MINUTES);
        return loginAttemptRepository.countFailedAttemptsByUsernameSince(username, cutoffTime);
    }

    /**
     * Récupère le nombre de tentatives restantes avant blocage
     *
     * @param username Nom d'utilisateur
     * @return Nombre de tentatives restantes
     */
    public int getRemainingAttempts(String username) {
        long failedAttempts = getFailedAttemptsByUsername(username);
        int remaining = MAX_ATTEMPTS_PER_USERNAME - (int) failedAttempts;
        return Math.max(0, remaining);
    }

    /**
     * Récupère la dernière connexion réussie pour un utilisateur
     *
     * @param username Nom d'utilisateur
     * @return LoginAttempt ou null si aucune connexion réussie
     */
    @Transactional(readOnly = true)
    public LoginAttempt getLastSuccessfulLogin(String username) {
        return loginAttemptRepository.findLastSuccessfulLogin(username);
    }

    /**
     * Récupère l'historique des connexions récentes pour un utilisateur
     *
     * @param username Nom d'utilisateur
     * @param days Nombre de jours d'historique
     * @return Liste des tentatives
     */
    @Transactional(readOnly = true)
    public List<LoginAttempt> getLoginHistory(String username, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return loginAttemptRepository.findByUsernameAndAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(username, since);
    }

    /**
     * Nettoie les anciennes tentatives de connexion (plus de 30 jours)
     * À appeler périodiquement via un scheduled task
     */
    @Transactional
    public void cleanupOldAttempts() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        loginAttemptRepository.deleteByAttemptTimeBefore(cutoffDate);
        logger.info("Nettoyage des tentatives de connexion antérieures au {}", cutoffDate);
    }

    /**
     * Récupère les informations de blocage pour affichage à l'utilisateur
     *
     * @param username Nom d'utilisateur
     * @return Message de blocage avec détails
     */
    public String getBlockMessage(String username) {
        long failedAttempts = getFailedAttemptsByUsername(username);

        if (failedAttempts >= MAX_ATTEMPTS_PER_USERNAME) {
            return String.format(
                "Votre compte a été temporairement bloqué en raison de %d tentatives de connexion échouées. " +
                "Veuillez réessayer dans %d minutes ou contactez l'administrateur.",
                failedAttempts, BLOCK_DURATION_MINUTES
            );
        }

        int remainingAttempts = getRemainingAttempts(username);
        return String.format(
            "Identifiants invalides. Il vous reste %d tentative(s) avant le blocage temporaire du compte.",
            remainingAttempts
        );
    }
}
