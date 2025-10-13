package com.docker.repository;

import com.docker.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ArticleRepository
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ArticleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void testFindAllByOrderByDatePublicationDesc_ReturnsArticlesInOrder() {
        // Given
        Article article1 = new Article();
        article1.setTitre("Article 1");
        article1.setContenu("This is test content for article 1 with enough characters for validation.");
        article1.setDatePublication(LocalDateTime.now().minusDays(2));
        entityManager.persist(article1);

        Article article2 = new Article();
        article2.setTitre("Article 2");
        article2.setContenu("This is test content for article 2 with enough characters for validation.");
        article2.setDatePublication(LocalDateTime.now().minusDays(1));
        entityManager.persist(article2);

        entityManager.flush();

        // When
        List<Article> articles = articleRepository.findAllByOrderByDatePublicationDesc();

        // Then
        assertTrue(articles.size() >= 2);
        assertTrue(articles.get(0).getDatePublication().isAfter(articles.get(1).getDatePublication()));
    }

    @Test
    void testSaveArticle_Success() {
        // Given
        Article article = new Article();
        article.setTitre("Test Article");
        article.setContenu("This is test article content with sufficient length for validation.");
        article.setDatePublication(LocalDateTime.now());

        // When
        Article saved = articleRepository.save(article);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Test Article", saved.getTitre());
    }
}
