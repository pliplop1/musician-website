package com.docker.config;

import com.docker.entity.Badge;
import com.docker.entity.BadgeType;
import com.docker.repository.BadgeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initialise les badges de base au démarrage de l'application.
 */
@Component
@Profile("dev")
@Slf4j
public class BadgeInitializer implements CommandLineRunner {

    private final BadgeRepository badgeRepository;

    public BadgeInitializer(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🎖️ Initialisation des badges...");

        // Badges basés sur les concerts favoris
        createBadgeIfNotFound(
            "FIRST_FAVORITE",
            "🎵 Mélomane",
            "Ajoutez votre premier concert favori",
            "🎵",
            BadgeType.FAVORITE_CONCERTS,
            1,
            1
        );

        createBadgeIfNotFound(
            "FIVE_FAVORITES",
            "🎸 Fan",
            "Marquez 5 concerts comme favoris",
            "🎸",
            BadgeType.FAVORITE_CONCERTS,
            5,
            2
        );

        createBadgeIfNotFound(
            "TEN_FAVORITES",
            "⭐ VIP",
            "Marquez 10 concerts comme favoris",
            "⭐",
            BadgeType.FAVORITE_CONCERTS,
            10,
            3
        );

        createBadgeIfNotFound(
            "TWENTY_FAVORITES",
            "👑 Super Fan",
            "Marquez 20 concerts comme favoris",
            "👑",
            BadgeType.FAVORITE_CONCERTS,
            20,
            3
        );

        // Badges d'ancienneté (basés sur les jours)
        createBadgeIfNotFound(
            "SEVEN_DAYS",
            "🌱 Nouveau",
            "Compte actif depuis 7 jours",
            "🌱",
            BadgeType.ACCOUNT_AGE,
            7,
            1
        );

        createBadgeIfNotFound(
            "THIRTY_DAYS",
            "🌿 Régulier",
            "Compte actif depuis 30 jours",
            "🌿",
            BadgeType.ACCOUNT_AGE,
            30,
            2
        );

        createBadgeIfNotFound(
            "ONE_YEAR",
            "🌳 Ancien",
            "Compte actif depuis 1 an",
            "🌳",
            BadgeType.ACCOUNT_AGE,
            365,
            3
        );

        log.info("✅ Badges initialisés avec succès");
    }

    private void createBadgeIfNotFound(String code, String name, String description,
                                        String icon, BadgeType type, Integer requiredCount, Integer level) {
        if (badgeRepository.findByCode(code).isEmpty()) {
            Badge badge = new Badge(code, name, description, icon, type, requiredCount, level);
            badgeRepository.save(badge);
            log.debug("Badge créé : {}", name);
        }
    }
}
