package com.docker.config;

import com.docker.entity.SocialLink;
import com.docker.repository.SocialLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initialise les réseaux sociaux par défaut au démarrage de l'application.
 */
@Component
@Profile("dev")
@Slf4j
public class SocialLinkInitializer implements CommandLineRunner {

    private final SocialLinkRepository socialLinkRepository;

    public SocialLinkInitializer(SocialLinkRepository socialLinkRepository) {
        this.socialLinkRepository = socialLinkRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🔗 Initialisation des réseaux sociaux...");

        // Créer les réseaux sociaux par défaut s'ils n'existent pas
        createSocialLinkIfNotFound("Facebook", "fab fa-facebook", 1);
        createSocialLinkIfNotFound("Instagram", "fab fa-instagram", 2);
        createSocialLinkIfNotFound("YouTube", "fab fa-youtube", 3);
        createSocialLinkIfNotFound("Twitter", "fab fa-twitter", 4);
        createSocialLinkIfNotFound("TikTok", "fab fa-tiktok", 5);
        createSocialLinkIfNotFound("Spotify", "fab fa-spotify", 6);
        createSocialLinkIfNotFound("SoundCloud", "fab fa-soundcloud", 7);
        createSocialLinkIfNotFound("LinkedIn", "fab fa-linkedin", 8);

        log.info("✅ Réseaux sociaux initialisés avec succès");
    }

    private void createSocialLinkIfNotFound(String name, String icon, Integer displayOrder) {
        if (socialLinkRepository.findByName(name).isEmpty()) {
            SocialLink socialLink = new SocialLink(name, icon, displayOrder);
            socialLinkRepository.save(socialLink);
            log.debug("Réseau social créé : {}", name);
        }
    }
}
