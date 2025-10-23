package com.docker.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.docker.repository.*;

/**
 * Classe de base pour les tests d'intégration
 *
 * Utilise une vraie base de données H2 en mémoire et un contexte Spring complet.
 * Tous les tests sont transactionnels et rollback automatiquement après chaque test.
 *
 * Cette approche valide les compétences CDA:
 * - CP06: Développer les composants d'accès aux données
 * - CP07: Développer des tests d'intégration
 * - CP08: Réaliser les tests d'intégration des composants
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    // Repositories pour préparer les données de test
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ConcertRepository concertRepository;

    @Autowired
    protected PhotoRepository photoRepository;

    @Autowired
    protected TrackRepository trackRepository;

    @Autowired
    protected MessageRepository messageRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected ArticleRepository articleRepository;

    @Autowired
    protected BiographyRepository biographyRepository;

    @Autowired
    protected SocialLinkRepository socialLinkRepository;

    /**
     * Nettoyage avant chaque test
     * Les transactions rollback automatiquement, mais on nettoie explicitement
     * pour éviter les interférences entre tests
     */
    @BeforeEach
    void cleanupDatabase() {
        // Les @Transactional s'occupent du rollback
        // Cette méthode peut être surchargée si besoin
    }
}
