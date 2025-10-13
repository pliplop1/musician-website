package com.docker.service;

import com.docker.entity.Biography;
import com.docker.repository.BiographyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour BiographyService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BiographyServiceTest {

    @Autowired
    private BiographyService biographyService;

    @Autowired
    private BiographyRepository biographyRepository;

    @Test
    void testGetBiography_FirstTime_CreatesDefaultBiography() {
        // When - Première récupération, devrait créer une biographie par défaut
        Biography biography = biographyService.getBiography();

        // Then
        assertNotNull(biography);
        assertNotNull(biography.getContent());
        assertEquals(1L, biography.getId());
        assertTrue(biography.getContent().contains("biographie"));
    }

    @Test
    void testGetBiography_ExistingBiography_ReturnsSameBiography() {
        // Given - Créer une biographie
        Biography firstCall = biographyService.getBiography();
        String originalContent = firstCall.getContent();

        // When - Récupérer à nouveau
        Biography secondCall = biographyService.getBiography();

        // Then - Devrait retourner la même biographie
        assertNotNull(secondCall);
        assertEquals(1L, secondCall.getId());
        assertEquals(originalContent, secondCall.getContent());
    }

    @Test
    void testSaveBiography_NewContent_UpdatesSuccessfully() {
        // Given
        String newContent = "Duo Black & White est un duo de musiciens talentueux formé en 2015...";

        // When
        biographyService.saveBiography(newContent);

        // Then
        Biography saved = biographyService.getBiography();
        assertNotNull(saved);
        assertEquals(newContent, saved.getContent());
        assertEquals(1L, saved.getId());
    }

    @Test
    void testSaveBiography_MultipleUpdates_KeepsSameId() {
        // Given
        String content1 = "Premier contenu";
        String content2 = "Deuxième contenu";
        String content3 = "Troisième contenu";

        // When - Plusieurs mises à jour successives
        biographyService.saveBiography(content1);
        Biography after1 = biographyService.getBiography();

        biographyService.saveBiography(content2);
        Biography after2 = biographyService.getBiography();

        biographyService.saveBiography(content3);
        Biography after3 = biographyService.getBiography();

        // Then - L'ID devrait rester 1, seul le contenu change
        assertEquals(1L, after1.getId());
        assertEquals(1L, after2.getId());
        assertEquals(1L, after3.getId());

        assertEquals(content1, after1.getContent());
        assertEquals(content2, after2.getContent());
        assertEquals(content3, after3.getContent());
    }

    @Test
    void testSaveBiography_EmptyContent_SavesSuccessfully() {
        // Given
        String emptyContent = "";

        // When
        biographyService.saveBiography(emptyContent);

        // Then
        Biography saved = biographyService.getBiography();
        assertNotNull(saved);
        assertEquals(emptyContent, saved.getContent());
    }

    @Test
    void testSaveBiography_LongContent_SavesSuccessfully() {
        // Given - Contenu long (plus de 1000 caractères)
        String longContent = "Duo Black & White est un duo exceptionnel. ".repeat(50);

        // When
        biographyService.saveBiography(longContent);

        // Then
        Biography saved = biographyService.getBiography();
        assertNotNull(saved);
        assertEquals(longContent, saved.getContent());
        assertTrue(saved.getContent().length() > 1000);
    }

    @Test
    void testGetBiography_AlwaysReturnsSameInstance() {
        // When - Plusieurs appels successifs
        Biography bio1 = biographyService.getBiography();
        Biography bio2 = biographyService.getBiography();
        Biography bio3 = biographyService.getBiography();

        // Then - Tous devraient avoir le même ID
        assertEquals(bio1.getId(), bio2.getId());
        assertEquals(bio2.getId(), bio3.getId());
        assertEquals(1L, bio1.getId());
    }

    @Test
    void testSaveBiography_WithHtmlContent_SavesSuccessfully() {
        // Given - Contenu avec HTML
        String htmlContent = "<h1>Duo Black & White</h1><p>Notre histoire commence en <b>2015</b>...</p>";

        // When
        biographyService.saveBiography(htmlContent);

        // Then
        Biography saved = biographyService.getBiography();
        assertNotNull(saved);
        assertEquals(htmlContent, saved.getContent());
        assertTrue(saved.getContent().contains("<h1>"));
        assertTrue(saved.getContent().contains("<b>"));
    }

    @Test
    void testSaveBiography_WithSpecialCharacters_SavesSuccessfully() {
        // Given - Contenu avec caractères spéciaux
        String specialContent = "Duo Black & White : l'histoire d'un duo exceptionnel ! "
                + "Formé à Paris (75), ils ont conquis le cœur de nombreux fans... "
                + "\"La musique, c'est notre vie\" - disent-ils.";

        // When
        biographyService.saveBiography(specialContent);

        // Then
        Biography saved = biographyService.getBiography();
        assertNotNull(saved);
        assertEquals(specialContent, saved.getContent());
    }
}
