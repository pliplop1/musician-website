package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.Article;
import com.docker.service.ArticleService;

@Controller
@RequestMapping("/admin")
public class AdminArticleController {

	private final ArticleService articleService;

	public AdminArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}

	@GetMapping("/articles")
	public String showArticleManagementPage(Model model) {
		model.addAttribute("articles", articleService.findAllArticles());
		if (!model.containsAttribute("article")) {
			model.addAttribute("article", new Article());
		}
		return "admin/article-management";
	}

	@PostMapping("/articles/add")
	public String saveArticle(@ModelAttribute Article article, RedirectAttributes redirectAttributes) {
		articleService.saveArticle(article);
		redirectAttributes.addFlashAttribute("successMessage", "L'article a été ajouté avec succès !");
		return "redirect:/admin/dashboard";
	}

	@PostMapping("/articles/delete/{id}")
	public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		articleService.deleteArticle(id);
		redirectAttributes.addFlashAttribute("successMessage", "L'article a été supprimé avec succès !");
		return "redirect:/admin/dashboard";
	}

	@GetMapping("/articles/edit/{id}")
	public String showEditArticleForm(@PathVariable Long id, Model model) {
		Article article = articleService.findArticleById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid article Id:" + id));
		model.addAttribute("article", article);
		return "admin/edit-article";
	}

	@PostMapping("/articles/update/{id}")
	public String updateArticle(@PathVariable Long id, @ModelAttribute Article article,
			RedirectAttributes redirectAttributes) {
		Article existingArticle = articleService.findArticleById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid article Id:" + id));
		existingArticle.setTitre(article.getTitre());
		existingArticle.setContenu(article.getContenu());
		articleService.saveArticle(existingArticle);
		redirectAttributes.addFlashAttribute("successMessage", "L'article a été modifié avec succès !");
		return "redirect:/admin/dashboard";
	}
}