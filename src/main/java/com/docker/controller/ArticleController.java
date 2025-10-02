package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.docker.service.ArticleService;

@Controller
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/actualites")
    public String showNewsList(Model model) {
        model.addAttribute("articles", articleService.findAllArticles());
        return "actualites"; // Le nom de notre nouvelle page HTML
    }
}