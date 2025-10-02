package com.docker.controller;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.Article;
import com.docker.entity.Concert;
import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.RoleRepository;
import com.docker.service.ArticleService;
import com.docker.service.ConcertService;
import com.docker.service.MessageService;
import com.docker.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ConcertService concertService;
    private final MessageService messageService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final ArticleService articleService;

    public AdminController(ConcertService concertService, MessageService messageService, UserService userService, RoleRepository roleRepository, ArticleService articleService) {
        this.concertService = concertService;
        this.messageService = messageService;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.articleService = articleService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Ajout des statistiques au modèle
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("concertCount", concertService.countConcerts());
        model.addAttribute("articleCount", articleService.countArticles());
        model.addAttribute("messageCount", messageService.countMessages());

        // On garde le reste de la logique pour les listes et formulaires
        model.addAttribute("concerts", concertService.findAllConcerts());
        model.addAttribute("messages", messageService.findAllMessages());
        model.addAttribute("articles", articleService.findAllArticles());

        if (!model.containsAttribute("concert")) {
            model.addAttribute("concert", new Concert());
        }
        if (!model.containsAttribute("article")) {
            model.addAttribute("article", new Article());
        }
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String showUserManagement(Model model, 
                                     @RequestParam(defaultValue = "username") String sortField,
                                     @RequestParam(defaultValue = "asc") String sortDir,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField));
        
        Page<User> userPage = userService.searchUsers(keyword, pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        
        return "admin/user-management";
    }

    @GetMapping("/users/edit/{id}")
    public String showUserEditForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/edit-user";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user, @RequestParam(name = "roleId", required = false) Long roleId, RedirectAttributes redirectAttributes) {
        userService.updateUserFromAdmin(user.getId(), user.getUsername(), user.getEmail(), Set.of(roleRepository.findById(roleId).orElse(null)));
        redirectAttributes.addFlashAttribute("successMessage", "L'utilisateur a été mis à jour avec succès.");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "L'utilisateur a été supprimé avec succès !");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/concerts/add")
    public String saveConcert(@ModelAttribute Concert concert, RedirectAttributes redirectAttributes) {
        concertService.saveConcert(concert);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été ajouté avec succès !");
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/concerts/delete/{id}")
    public String deleteConcert(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        concertService.deleteConcert(id);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été supprimé avec succès !");
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        messageService.deleteMessage(id);
        redirectAttributes.addFlashAttribute("successMessage", "Le message a été supprimé avec succès !");
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/concerts/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Concert concert = concertService.findConcertById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));
        model.addAttribute("concert", concert);
        return "admin/edit-concert";
    }

    @PostMapping("/concerts/update/{id}")
    public String updateConcert(@PathVariable Long id, @ModelAttribute Concert concert, RedirectAttributes redirectAttributes) {
        Concert existingConcert = concertService.findConcertById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));
        existingConcert.setLocation(concert.getLocation());
        existingConcert.setDate(concert.getDate());
        existingConcert.setDescription(concert.getDescription());
        
        concertService.saveConcert(existingConcert);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été modifié avec succès !");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/articles/add")
    public String saveArticle(@ModelAttribute Article article, RedirectAttributes redirectAttributes) {
        articleService.saveArticle(article);
        redirectAttributes.addFlashAttribute("successMessage", "L'article a été ajouté avec succès !");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/articles/delete/{id}")
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
    public String updateArticle(@PathVariable Long id, @ModelAttribute Article article, RedirectAttributes redirectAttributes) {
        Article existingArticle = articleService.findArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid article Id:" + id));
        
        existingArticle.setTitre(article.getTitre());
        existingArticle.setContenu(article.getContenu());
        
        articleService.saveArticle(existingArticle);
        redirectAttributes.addFlashAttribute("successMessage", "L'article a été modifié avec succès !");
        return "redirect:/admin/dashboard";
    }
}