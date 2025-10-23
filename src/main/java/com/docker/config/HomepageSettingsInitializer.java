package com.docker.config;

import com.docker.entity.HomepageSettings;
import com.docker.repository.HomepageSettingsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

/**
 * Initialise les paramètres de la page d'accueil par défaut au démarrage
 * S'exécute uniquement si aucun paramètre n'existe déjà
 */
@Configuration
public class HomepageSettingsInitializer {

    /**
     * Création des paramètres par défaut si inexistants
     * @Order(5) pour s'exécuter après les autres initializers (badges, social links, etc.)
     */
    @Bean
    @Profile({"dev", "prod"})
    @Order(5)
    public CommandLineRunner initializeHomepageSettings(
            HomepageSettingsRepository homepageSettingsRepository) {

        return args -> {
            // Vérifier si des paramètres existent déjà
            long count = homepageSettingsRepository.count();

            if (count == 0) {
                System.out.println("=== Initialisation des paramètres de la page d'accueil Vue.js ===");

                // Créer les paramètres par défaut
                HomepageSettings settings = new HomepageSettings(
                    "DUO BLACK & WHITE",
                    "La musique qui vous transporte • France & Belgique"
                );

                // Paramètres optionnels
                settings.setWelcomeMessage("Bienvenue sur le site officiel du Duo Black & White ! " +
                        "Découvrez notre univers musical, nos concerts à venir et rejoignez notre communauté.");
                settings.setRegistrationMessage("Rejoignez notre communauté de fans pour ne rien manquer " +
                        "de nos actualités et recevoir des invitations exclusives !");
                settings.setRegistrationEnabled(true);

                // Sauvegarder
                homepageSettingsRepository.save(settings);

                System.out.println("✓ Paramètres de la page d'accueil créés avec succès");
                System.out.println("  - Titre: " + settings.getHeroTitle());
                System.out.println("  - Sous-titre: " + settings.getHeroSubtitle());
                System.out.println("  - Inscription publique: " + (settings.getRegistrationEnabled() ? "Activée" : "Désactivée"));
                System.out.println("========================================");
            } else {
                System.out.println("✓ Paramètres de la page d'accueil déjà existants (skip)");
            }
        };
    }
}
