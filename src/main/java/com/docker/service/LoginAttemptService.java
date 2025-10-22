package com.docker.service;

import com.docker.dto.DailyStatsDTO;
import com.docker.dto.LoginAttemptDTO;
import com.docker.dto.SecurityStatsDTO;
import com.docker.dto.SuspiciousIpDTO;
import com.docker.entity.LoginAttempt;
import com.docker.repository.LoginAttemptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
        return loginAttemptRepository.findFirstByUsernameAndSuccessTrueOrderByAttemptTimeDesc(username);
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
     * Récupère l'historique paginé des connexions pour un utilisateur
     *
     * @param username Nom d'utilisateur
     * @param days Nombre de jours d'historique
     * @param pageable Paramètres de pagination et tri
     * @return Page de tentatives
     */
    @Transactional(readOnly = true)
    public Page<LoginAttempt> getLoginHistoryPaginated(String username, int days, Pageable pageable) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return loginAttemptRepository.findByUsernameAndAttemptTimeGreaterThanEqual(username, since, pageable);
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

    // ========================================
    // MÉTHODES POUR LE DASHBOARD ADMIN
    // ========================================

    /**
     * Récupère les statistiques complètes de sécurité pour le dashboard admin
     *
     * @return SecurityStatsDTO avec toutes les statistiques
     */
    @Transactional(readOnly = true)
    public SecurityStatsDTO getSecurityStats() {
        SecurityStatsDTO stats = new SecurityStatsDTO();

        // Statistiques globales
        stats.setTotalAttempts(loginAttemptRepository.count());
        stats.setSuccessfulAttempts(loginAttemptRepository.countBySuccess(true));
        stats.setFailedAttempts(loginAttemptRepository.countBySuccess(false));
        stats.setBlockedAccounts(countCurrentlyBlockedAccounts());

        // 50 dernières tentatives
        List<LoginAttempt> recentAttempts = loginAttemptRepository.findTop50ByOrderByAttemptTimeDesc();
        stats.setRecentAttempts(convertToLoginAttemptDTOs(recentAttempts));

        // IPs suspectes (plus de 3 échecs dans les dernières 24h)
        stats.setSuspiciousIps(getSuspiciousIps());

        // Statistiques par jour (7 derniers jours)
        stats.setDailyStats(getDailyStats(7));

        return stats;
    }

    /**
     * Compte le nombre de comptes actuellement bloqués
     *
     * @return Nombre de comptes bloqués
     */
    @Transactional(readOnly = true)
    public long countCurrentlyBlockedAccounts() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(BLOCK_DURATION_MINUTES);

        // Récupère tous les usernames avec échecs récents
        List<String> usernamesWithFailures = loginAttemptRepository
            .findDistinctUsernamesBySuccessFalseAndAttemptTimeGreaterThanEqual(cutoffTime);

        // Compte ceux qui sont effectivement bloqués
        return usernamesWithFailures.stream()
            .filter(this::isBlocked)
            .count();
    }

    /**
     * Récupère les IPs suspectes avec nombreuses tentatives échouées
     *
     * @return Liste des IPs suspectes
     */
    @Transactional(readOnly = true)
    public List<SuspiciousIpDTO> getSuspiciousIps() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);

        // Récupère toutes les tentatives des dernières 24h
        List<LoginAttempt> recentAttempts = loginAttemptRepository
            .findByAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(last24Hours);

        // Groupe par IP
        Map<String, List<LoginAttempt>> attemptsByIp = recentAttempts.stream()
            .collect(Collectors.groupingBy(LoginAttempt::getIpAddress));

        // Filtre les IPs avec plus de 3 échecs
        List<SuspiciousIpDTO> suspiciousIps = new ArrayList<>();

        attemptsByIp.forEach((ip, attempts) -> {
            long failedCount = attempts.stream().filter(a -> !a.isSuccess()).count();
            long successCount = attempts.stream().filter(LoginAttempt::isSuccess).count();

            if (failedCount >= 3) {
                LoginAttempt lastAttempt = attempts.get(0); // Liste déjà triée par date desc
                SuspiciousIpDTO dto = new SuspiciousIpDTO(
                    ip,
                    failedCount,
                    successCount,
                    lastAttempt.getUsername(),
                    lastAttempt.getAttemptTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                );
                suspiciousIps.add(dto);
            }
        });

        // Trie par nombre d'échecs décroissant
        suspiciousIps.sort((a, b) -> Long.compare(b.getFailedAttempts(), a.getFailedAttempts()));

        return suspiciousIps;
    }

    /**
     * Récupère les statistiques journalières pour les N derniers jours
     *
     * @param days Nombre de jours
     * @return Map avec les statistiques par jour
     */
    @Transactional(readOnly = true)
    public Map<String, DailyStatsDTO> getDailyStats(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days).withHour(0).withMinute(0).withSecond(0);
        List<LoginAttempt> attempts = loginAttemptRepository
            .findByAttemptTimeGreaterThanEqualOrderByAttemptTimeDesc(startDate);

        // Groupe par jour
        Map<LocalDate, List<LoginAttempt>> attemptsByDay = attempts.stream()
            .collect(Collectors.groupingBy(a -> a.getAttemptTime().toLocalDate()));

        // Convertit en DailyStatsDTO
        Map<String, DailyStatsDTO> dailyStats = new LinkedHashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Crée une entrée pour chaque jour (même si pas d'activité)
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateKey = date.format(dateFormatter);

            List<LoginAttempt> dayAttempts = attemptsByDay.getOrDefault(date, Collections.emptyList());

            long total = dayAttempts.size();
            long successful = dayAttempts.stream().filter(LoginAttempt::isSuccess).count();
            long failed = dayAttempts.stream().filter(a -> !a.isSuccess()).count();

            dailyStats.put(dateKey, new DailyStatsDTO(dateKey, total, successful, failed));
        }

        return dailyStats;
    }

    /**
     * Convertit les entités LoginAttempt en DTOs
     *
     * @param attempts Liste d'entités
     * @return Liste de DTOs
     */
    private List<LoginAttemptDTO> convertToLoginAttemptDTOs(List<LoginAttempt> attempts) {
        return attempts.stream()
            .map(a -> new LoginAttemptDTO(
                a.getId(),
                a.getUsername(),
                a.getIpAddress(),
                a.getAttemptTime(),
                a.isSuccess(),
                a.getFailureReason(),
                a.getUserAgent()
            ))
            .collect(Collectors.toList());
    }
}
