package com.docker.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import com.docker.repository.*;
import com.docker.service.*;
import com.docker.controller.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration du contexte Spring
 *
 * Vérifie que l'application Spring Boot se charge correctement avec:
 * - Tous les beans sont créés
 * - La configuration de sécurité est chargée
 * - Les repositories sont accessibles
 * - Les services sont injectés correctement
 *
 * Valide les compétences CDA:
 * - CP05: Concevoir et développer une application multicouche répartie
 * - CP08: Réaliser les tests d'intégration des composants
 * - CP09: Préparer l'environnement de test et d'intégration continue
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Tests d'intégration - Contexte Spring")
class ApplicationContextIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Le contexte Spring doit se charger sans erreur")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    // Tests des Repositories
    @Test
    @DisplayName("Tous les repositories doivent être injectés")
    void testRepositoriesAreInjected() {
        assertThat(applicationContext.getBean(UserRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(ConcertRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(PhotoRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(TrackRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(MessageRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(CommentRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(ArticleRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(BiographyRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(SocialLinkRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(PlaylistRepository.class)).isNotNull();
        assertThat(applicationContext.getBean(RoleRepository.class)).isNotNull();
    }

    // Tests des Services
    @Test
    @DisplayName("Tous les services métier doivent être injectés")
    void testServicesAreInjected() {
        assertThat(applicationContext.getBean(UserService.class)).isNotNull();
        assertThat(applicationContext.getBean(ConcertService.class)).isNotNull();
        assertThat(applicationContext.getBean(PhotoService.class)).isNotNull();
        assertThat(applicationContext.getBean(TrackService.class)).isNotNull();
        assertThat(applicationContext.getBean(MessageService.class)).isNotNull();
        assertThat(applicationContext.getBean(CommentService.class)).isNotNull();
        assertThat(applicationContext.getBean(ArticleService.class)).isNotNull();
        assertThat(applicationContext.getBean(BiographyService.class)).isNotNull();
        assertThat(applicationContext.getBean(SocialLinkService.class)).isNotNull();
        assertThat(applicationContext.getBean(PlaylistService.class)).isNotNull();
        assertThat(applicationContext.getBean(EmailService.class)).isNotNull();
        assertThat(applicationContext.getBean(PasswordResetService.class)).isNotNull();
    }

    // Tests des Controllers
    @Test
    @DisplayName("Tous les contrôleurs doivent être injectés")
    void testControllersAreInjected() {
        assertThat(applicationContext.getBean(MainController.class)).isNotNull();
        assertThat(applicationContext.getBean(LoginController.class)).isNotNull();
        assertThat(applicationContext.getBean(RegistrationController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminConcertController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminPhotoController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminTrackController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminMessageController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminCommentController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminArticleController.class)).isNotNull();
        assertThat(applicationContext.getBean(AdminBiographyController.class)).isNotNull();
    }

    @Test
    @DisplayName("La configuration de sécurité doit être chargée")
    void testSecurityConfigurationIsLoaded() {
        // Vérifier que les beans de sécurité existent
        assertThat(applicationContext.containsBean("passwordEncoder")).isTrue();
        assertThat(applicationContext.containsBean("securityFilterChain")).isTrue();
    }
}
