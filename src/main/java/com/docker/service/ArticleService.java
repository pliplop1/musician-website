package com.docker.service;

import com.docker.entity.Article;
import com.docker.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * Récupère tous les articles, triés par date de publication la plus récente.
     * @return Une liste d'articles.
     */
    public List<Article> findAllArticles() {
        return articleRepository.findAllByOrderByDatePublicationDesc();
    }

    /**
     * Trouve un article par son ID.
     * @param id L'ID de l'article.
     * @return Un Optional contenant l'article s'il est trouvé.
     */
    public Optional<Article> findArticleById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * Sauvegarde un article (création ou mise à jour).
     * Si c'est un nouvel article (pas d'ID), la date de publication est automatiquement définie.
     * @param article L'article à sauvegarder.
     * @return L'article sauvegardé.
     */
    public Article saveArticle(Article article) {
        if (article.getId() == null) {
            article.setDatePublication(LocalDateTime.now());
        }
        return articleRepository.save(article);
    }

    /**
     * Supprime un article par son ID.
     * @param id L'ID de l'article à supprimer.
     */
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
    public long countArticles() {
        return articleRepository.count();
    }
}