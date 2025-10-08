package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docker.entity.Article;
import com.docker.entity.Concert;
import com.docker.service.ArticleService;
import com.docker.service.BiographyService;
import com.docker.service.ConcertService;
import com.docker.service.MessageService;
import com.docker.service.PhotoService;
import com.docker.service.TrackService;
import com.docker.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // On garde uniquement les services nécessaires pour l'affichage du dashboard
    private final UserService userService;
    private final ConcertService concertService;
    private final ArticleService articleService;
    private final MessageService messageService;
    private final BiographyService biographyService;
    private final PhotoService photoService;
    private final TrackService trackService;

    // Le constructeur est maintenant beaucoup plus simple !
    public AdminController(UserService userService, ConcertService concertService, ArticleService articleService,
            MessageService messageService, BiographyService biographyService, PhotoService photoService,
            TrackService trackService) {
        this.userService = userService;
        this.concertService = concertService;
        this.articleService = articleService;
        this.messageService = messageService;
        this.biographyService = biographyService;
        this.photoService = photoService;
        this.trackService = trackService;
    }

    /**
     * Affiche le tableau de bord principal avec les statistiques et les listes
     * d'aperçu.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Ajout des statistiques
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("concertCount", concertService.countConcerts());
        model.addAttribute("articleCount", articleService.countArticles());
        model.addAttribute("messageCount", messageService.countMessages());

        // Ajout des listes et autres éléments nécessaires à la vue
        model.addAttribute("biography", biographyService.getBiography());
        model.addAttribute("photos", photoService.getAllPhotos());
        model.addAttribute("concerts", concertService.findAllConcerts());
        model.addAttribute("messages", messageService.findAllMessages());
        model.addAttribute("articles", articleService.findAllArticles());
        model.addAttribute("tracks", trackService.getAllTracks());

        // Initialise les objets pour les formulaires d'ajout du dashboard
        if (!model.containsAttribute("concert")) {
            model.addAttribute("concert", new Concert());
        }
        if (!model.containsAttribute("article")) {
            model.addAttribute("article", new Article());
        }

        return "admin/dashboard";
    }
}