package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.docker.service.ArticleService;
import com.docker.service.CommentService;
import com.docker.entity.CommentType;

@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    public ArticleController(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @GetMapping("/actualites")
    public String showNewsList(Model model) {
        var articles = articleService.findAllArticles();
        model.addAttribute("articles", articles);

        // Ajouter les compteurs et commentaires pour chaque article
        articles.forEach(article -> {
            Long commentCount = commentService.countApprovedComments(CommentType.ARTICLE, article.getId());
            model.addAttribute("commentCount_" + article.getId(), commentCount);

            var comments = commentService.getApprovedComments(CommentType.ARTICLE, article.getId());
            model.addAttribute("comments_" + article.getId(), comments);
        });

        return "actualites"; // Le nom de notre nouvelle page HTML
    }
}