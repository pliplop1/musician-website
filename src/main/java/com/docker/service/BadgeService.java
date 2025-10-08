package com.docker.service;

import com.docker.entity.*;
import com.docker.repository.BadgeRepository;
import com.docker.repository.UserBadgeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les badges et leur déblocage automatique.
 */
@Service
@Slf4j
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    /**
     * Vérifie et débloque automatiquement les badges pour un utilisateur.
     * @param user L'utilisateur à vérifier
     * @return Liste des nouveaux badges débloqués
     */
    @Transactional
    public List<Badge> checkAndUnlockBadges(User user) {
        List<Badge> newlyUnlockedBadges = new ArrayList<>();

        // Vérifier les badges de concerts favoris
        int favoriteConcertsCount = user.getFavoriteConcerts() != null ? user.getFavoriteConcerts().size() : 0;
        newlyUnlockedBadges.addAll(checkBadgesByType(user, BadgeType.FAVORITE_CONCERTS, favoriteConcertsCount));

        // Vérifier les badges d'ancienneté
        if (user.getCreatedAt() != null) {
            long daysOld = ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now());
            newlyUnlockedBadges.addAll(checkBadgesByType(user, BadgeType.ACCOUNT_AGE, (int) daysOld));
        }

        if (!newlyUnlockedBadges.isEmpty()) {
            log.info("🎖️ {} nouveau(x) badge(s) débloqué(s) pour l'utilisateur {}",
                newlyUnlockedBadges.size(), user.getUsername());
        }

        return newlyUnlockedBadges;
    }

    /**
     * Vérifie et débloque les badges d'un type spécifique.
     */
    private List<Badge> checkBadgesByType(User user, BadgeType type, int currentCount) {
        List<Badge> newlyUnlocked = new ArrayList<>();
        List<Badge> badgesOfType = badgeRepository.findByTypeOrderByRequiredCountAsc(type);

        for (Badge badge : badgesOfType) {
            // Si l'utilisateur a atteint le seuil et n'a pas encore ce badge
            if (currentCount >= badge.getRequiredCount()
                && !userBadgeRepository.existsByUserAndBadge(user, badge)) {

                UserBadge userBadge = new UserBadge(user, badge);
                userBadgeRepository.save(userBadge);
                newlyUnlocked.add(badge);

                log.info("✨ Badge '{}' débloqué pour {}", badge.getName(), user.getUsername());
            }
        }

        return newlyUnlocked;
    }

    /**
     * Récupère tous les badges d'un utilisateur.
     */
    public List<UserBadge> getUserBadges(User user) {
        return userBadgeRepository.findByUserOrderByUnlockedAtDesc(user);
    }

    /**
     * Récupère les badges non notifiés d'un utilisateur.
     */
    public List<UserBadge> getUnnotifiedBadges(User user) {
        return userBadgeRepository.findUnnotifiedBadges(user);
    }

    /**
     * Marque un badge comme notifié.
     */
    @Transactional
    public void markBadgeAsNotified(UserBadge userBadge) {
        userBadge.setNotified(true);
        userBadgeRepository.save(userBadge);
    }

    /**
     * Compte le nombre de badges d'un utilisateur.
     */
    public long countUserBadges(User user) {
        return userBadgeRepository.countByUser(user);
    }

    /**
     * Récupère tous les badges disponibles.
     */
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    /**
     * Vérifie si un utilisateur possède un badge spécifique.
     */
    public boolean userHasBadge(User user, String badgeCode) {
        return badgeRepository.findByCode(badgeCode)
            .map(badge -> userBadgeRepository.existsByUserAndBadge(user, badge))
            .orElse(false);
    }

    /**
     * Débloque manuellement un badge pour un utilisateur (pour les badges spéciaux).
     */
    @Transactional
    public void unlockBadge(User user, String badgeCode) {
        badgeRepository.findByCode(badgeCode).ifPresent(badge -> {
            if (!userBadgeRepository.existsByUserAndBadge(user, badge)) {
                UserBadge userBadge = new UserBadge(user, badge);
                userBadgeRepository.save(userBadge);
                log.info("🎖️ Badge spécial '{}' débloqué manuellement pour {}", badge.getName(), user.getUsername());
            }
        });
    }

    /**
     * Sauvegarde un badge.
     */
    @Transactional
    public Badge saveBadge(Badge badge) {
        return badgeRepository.save(badge);
    }
}
