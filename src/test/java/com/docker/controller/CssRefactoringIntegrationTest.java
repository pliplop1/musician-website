package com.docker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour valider la refactorisation CSS
 * Vérifie que :
 * 1. Les fichiers CSS externes existent
 * 2. Les fichiers CSS sont accessibles via HTTP
 * 3. Les templates incluent les bonnes références CSS
 * 4. Les fichiers de backup existent
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CssRefactoringIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String CSS_DIR = "src/main/resources/static/css/";
    private static final String TEMPLATES_DIR = "src/main/resources/templates/";

    // ========================================================================
    // TESTS DE L'EXISTENCE DES FICHIERS CSS
    // ========================================================================

    @Test
    void testMusicPageCssFileExists() {
        Path cssFile = Paths.get(CSS_DIR + "music-page.css");
        assertTrue(Files.exists(cssFile),
            "Le fichier music-page.css doit exister après la refactorisation");
    }

    @Test
    void testVideoPageCssFileExists() {
        Path cssFile = Paths.get(CSS_DIR + "video-page.css");
        assertTrue(Files.exists(cssFile),
            "Le fichier video-page.css doit exister après la refactorisation");
    }

    @Test
    void testGalleryPageCssFileExists() {
        Path cssFile = Paths.get(CSS_DIR + "gallery-page.css");
        assertTrue(Files.exists(cssFile),
            "Le fichier gallery-page.css doit exister après la refactorisation");
    }

    // ========================================================================
    // TESTS DE L'ACCESSIBILITÉ DES FICHIERS CSS VIA HTTP
    // ========================================================================

    @Test
    void testMusicPageCssIsAccessible() throws Exception {
        mockMvc.perform(get("/css/music-page.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/css"));
    }

    @Test
    void testVideoPageCssIsAccessible() throws Exception {
        mockMvc.perform(get("/css/video-page.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/css"));
    }

    @Test
    void testGalleryPageCssIsAccessible() throws Exception {
        mockMvc.perform(get("/css/gallery-page.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/css"));
    }

    // ========================================================================
    // TESTS DU CONTENU DES FICHIERS CSS
    // ========================================================================

    @Test
    void testMusicPageCssContainsExpectedStyles() throws Exception {
        String cssContent = mockMvc.perform(get("/css/music-page.css"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Vérifier que le CSS contient les classes essentielles
        assertTrue(cssContent.contains(".music-page"),
            "Le CSS doit contenir la classe .music-page");
        assertTrue(cssContent.contains(".track-card"),
            "Le CSS doit contenir la classe .track-card");
        assertTrue(cssContent.contains(".like-button"),
            "Le CSS doit contenir la classe .like-button");
        assertTrue(cssContent.contains("@keyframes"),
            "Le CSS doit contenir des animations");
    }

    @Test
    void testVideoPageCssContainsExpectedStyles() throws Exception {
        String cssContent = mockMvc.perform(get("/css/video-page.css"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Vérifier que le CSS contient les classes essentielles
        assertTrue(cssContent.contains(".videos-page"),
            "Le CSS doit contenir la classe .videos-page");
        assertTrue(cssContent.contains(".video-card"),
            "Le CSS doit contenir la classe .video-card");
        assertTrue(cssContent.contains(".play-overlay"),
            "Le CSS doit contenir la classe .play-overlay");
        assertTrue(cssContent.contains(".view-count-badge"),
            "Le CSS doit contenir la classe .view-count-badge");
    }

    @Test
    void testGalleryPageCssContainsExpectedStyles() throws Exception {
        String cssContent = mockMvc.perform(get("/css/gallery-page.css"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Vérifier que le CSS contient les classes essentielles
        assertTrue(cssContent.contains(".gallery-page"),
            "Le CSS doit contenir la classe .gallery-page");
        assertTrue(cssContent.contains(".photo-card"),
            "Le CSS doit contenir la classe .photo-card");
        assertTrue(cssContent.contains(".zoom-overlay"),
            "Le CSS doit contenir la classe .zoom-overlay");
        assertTrue(cssContent.contains(".photo-modal"),
            "Le CSS doit contenir la classe .photo-modal");
    }

    // ========================================================================
    // TESTS DES TEMPLATES HTML
    // ========================================================================

    @Test
    void testMusiqueHtmlReferencesExternalCss() throws Exception {
        Path htmlFile = Paths.get(TEMPLATES_DIR + "musique.html");
        assertTrue(Files.exists(htmlFile), "Le fichier musique.html doit exister");

        String htmlContent = Files.readString(htmlFile);

        // Vérifier que le template référence le CSS externe
        assertTrue(htmlContent.contains("th:href=\"@{/css/music-page.css}\""),
            "musique.html doit référencer music-page.css avec Thymeleaf");

        // Vérifier qu'il n'y a plus de balise <style> avec beaucoup de CSS inline
        assertFalse(htmlContent.contains("<style>\n/* Styles pour la page musique */"),
            "musique.html ne doit plus contenir le CSS inline dans une balise <style>");
    }

    @Test
    void testVideosHtmlReferencesExternalCss() throws Exception {
        Path htmlFile = Paths.get(TEMPLATES_DIR + "videos.html");
        assertTrue(Files.exists(htmlFile), "Le fichier videos.html doit exister");

        String htmlContent = Files.readString(htmlFile);

        // Vérifier que le template référence le CSS externe
        assertTrue(htmlContent.contains("th:href=\"@{/css/video-page.css}\""),
            "videos.html doit référencer video-page.css avec Thymeleaf");

        // Vérifier qu'il n'y a plus de balise <style> avec beaucoup de CSS inline
        assertFalse(htmlContent.contains("<style>\n/* Styles pour la page vidéos */"),
            "videos.html ne doit plus contenir le CSS inline dans une balise <style>");
    }

    @Test
    void testGalerieHtmlReferencesExternalCss() throws Exception {
        Path htmlFile = Paths.get(TEMPLATES_DIR + "galerie.html");
        assertTrue(Files.exists(htmlFile), "Le fichier galerie.html doit exister");

        String htmlContent = Files.readString(htmlFile);

        // Vérifier que le template référence le CSS externe
        assertTrue(htmlContent.contains("th:href=\"@{/css/gallery-page.css}\""),
            "galerie.html doit référencer gallery-page.css avec Thymeleaf");

        // Vérifier qu'il n'y a plus de balise <style> avec beaucoup de CSS inline
        assertFalse(htmlContent.contains("<style>\n/* Styles pour la page galerie */"),
            "galerie.html ne doit plus contenir le CSS inline dans une balise <style>");
    }

    // ========================================================================
    // TESTS DE L'EXISTENCE DES FICHIERS DE BACKUP
    // ========================================================================

    @Test
    void testMusiqueBackupFileExists() {
        Path backupFile = Paths.get(TEMPLATES_DIR + "musique.html.backup");
        assertTrue(Files.exists(backupFile),
            "Le fichier de backup musique.html.backup doit exister");
    }

    @Test
    void testVideosBackupFileExists() {
        Path backupFile = Paths.get(TEMPLATES_DIR + "videos.html.backup");
        assertTrue(Files.exists(backupFile),
            "Le fichier de backup videos.html.backup doit exister");
    }

    @Test
    void testGalerieBackupFileExists() {
        Path backupFile = Paths.get(TEMPLATES_DIR + "galerie.html.backup");
        assertTrue(Files.exists(backupFile),
            "Le fichier de backup galerie.html.backup doit exister");
    }

    // ========================================================================
    // TESTS DE TAILLE DES FICHIERS (vérifier la réduction)
    // ========================================================================

    @Test
    void testMusiqueHtmlIsSmallerThanBackup() throws Exception {
        Path originalFile = Paths.get(TEMPLATES_DIR + "musique.html");
        Path backupFile = Paths.get(TEMPLATES_DIR + "musique.html.backup");

        if (Files.exists(originalFile) && Files.exists(backupFile)) {
            long originalSize = Files.size(originalFile);
            long backupSize = Files.size(backupFile);

            assertTrue(originalSize < backupSize,
                String.format("Le fichier refactorisé musique.html (%d octets) doit être plus petit que le backup (%d octets)",
                    originalSize, backupSize));
        }
    }

    @Test
    void testVideosHtmlIsSmallerThanBackup() throws Exception {
        Path originalFile = Paths.get(TEMPLATES_DIR + "videos.html");
        Path backupFile = Paths.get(TEMPLATES_DIR + "videos.html.backup");

        if (Files.exists(originalFile) && Files.exists(backupFile)) {
            long originalSize = Files.size(originalFile);
            long backupSize = Files.size(backupFile);

            assertTrue(originalSize < backupSize,
                String.format("Le fichier refactorisé videos.html (%d octets) doit être plus petit que le backup (%d octets)",
                    originalSize, backupSize));
        }
    }

    @Test
    void testGalerieHtmlIsSmallerThanBackup() throws Exception {
        Path originalFile = Paths.get(TEMPLATES_DIR + "galerie.html");
        Path backupFile = Paths.get(TEMPLATES_DIR + "galerie.html.backup");

        if (Files.exists(originalFile) && Files.exists(backupFile)) {
            long originalSize = Files.size(originalFile);
            long backupSize = Files.size(backupFile);

            assertTrue(originalSize < backupSize,
                String.format("Le fichier refactorisé galerie.html (%d octets) doit être plus petit que le backup (%d octets)",
                    originalSize, backupSize));
        }
    }

    // ========================================================================
    // TESTS DE NON-RÉGRESSION (les pages se chargent toujours)
    // ========================================================================

    @Test
    void testMusiquePageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/musique"))
                .andExpect(status().isOk())
                .andExpect(view().name("musique"));
    }

    @Test
    void testVideosPageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("videos"));
    }

    @Test
    void testGaleriePageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/galerie"))
                .andExpect(status().isOk())
                .andExpect(view().name("galerie"));
    }
}
