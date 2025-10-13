package com.docker.service;

import com.docker.entity.Article;
import com.docker.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ArticleService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void testFindAllArticles_ReturnsAllArticles() {
        // Given
        createAndSaveArticle("Article 1", "This is test content for article 1 with enough characters.");
        createAndSaveArticle("Article 2", "This is test content for article 2 with enough characters.");

        // When
        List<Article> articles = articleService.findAllArticles();

        // Then
        assertTrue(articles.size() >= 2);
    }

    @Test
    void testFindArticleById_ExistingArticle_ReturnsArticle() {
        // Given
        Article article = createAndSaveArticle("Test Article", "This is test content with enough characters for validation.");

        // When
        Optional<Article> found = articleService.findArticleById(article.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test Article", found.get().getTitre());
    }

    @Test
    void testFindArticleById_NonExistingArticle_ReturnsEmpty() {
        // When
        Optional<Article> found = articleService.findArticleById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveArticle_NewArticle_SetsPublicationDate() {
        // Given
        Article article = new Article();
        article.setTitre("New Article");
        article.setContenu("This is new article content with sufficient length for validation.");

        // When
        Article saved = articleService.saveArticle(article);

        // Then
        assertNotNull(saved.getId());
        assertNotNull(saved.getDatePublication());
        assertEquals("New Article", saved.getTitre());
    }

    @Test
    void testSaveArticle_UpdateExisting_KeepsOriginalDate() {
        // Given
        Article original = createAndSaveArticle("Original", "This is original article content with enough characters for validation.");
        Long originalId = original.getId();
        var originalDate = original.getDatePublication();

        // When
        original.setTitre("Updated Title");
        Article updated = articleService.saveArticle(original);

        // Then
        assertEquals(originalId, updated.getId());
        assertEquals(originalDate, updated.getDatePublication());
        assertEquals("Updated Title", updated.getTitre());
    }

    @Test
    void testDeleteArticle_ExistingArticle_Success() {
        // Given
        Article article = createAndSaveArticle("Delete Me", "This article content will be deleted with enough characters.");
        Long articleId = article.getId();

        // When
        articleService.deleteArticle(articleId);

        // Then
        assertFalse(articleRepository.findById(articleId).isPresent());
    }

    @Test
    void testCountArticles_ReturnsCorrectCount() {
        // Given
        long initialCount = articleService.countArticles();
        createAndSaveArticle("Article 1", "This is test content for article 1 with enough characters.");
        createAndSaveArticle("Article 2", "This is test content for article 2 with enough characters.");

        // When
        long newCount = articleService.countArticles();

        // Then
        assertEquals(initialCount + 2, newCount);
    }

    /**
     * Helper method to create and save an article
     */
    private Article createAndSaveArticle(String titre, String contenu) {
        Article article = new Article();
        article.setTitre(titre);
        article.setContenu(contenu);
        return articleService.saveArticle(article);
    }
}
