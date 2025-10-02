package com.docker.repository;

import com.docker.entity.Article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Cette méthode nous sera très utile pour afficher les articles du plus récent au plus ancien
    List<Article> findAllByOrderByDatePublicationDesc();

}