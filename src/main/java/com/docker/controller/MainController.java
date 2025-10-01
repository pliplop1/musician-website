package com.docker.controller;

import com.docker.entity.Concert;
import com.docker.entity.Message;
import com.docker.service.ConcertService;
import com.docker.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Ce contrôleur gère les requêtes pour la page d'accueil.
 */
@Controller
public class MainController {

    private final ConcertService concertService;
    private final MessageService messageService;

    @Autowired
    public MainController(ConcertService concertService, MessageService messageService) {
        this.concertService = concertService;
        this.messageService = messageService;
    }

    /**
     * Gère les requêtes GET pour la page d'accueil ("/").
     *
     * @param model Le modèle pour passer des données à la vue.
     * @return Le nom de la vue (template Thymeleaf) à afficher.
     */
    @GetMapping("/")
    public String home(Model model) {
        List<Concert> concerts = concertService.findAllConcerts();
        model.addAttribute("concerts", concerts);
        // Assure qu'un objet message est toujours disponible pour le formulaire
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", new Message());
        }
        return "index";
    }

    /**
     * Gère la soumission du formulaire de contact.
     * @param message L'objet Message rempli à partir des données du formulaire.
     * @param redirectAttributes Pour passer un message de succès après la redirection.
     * @return Une redirection vers la page d'accueil.
     */
    @PostMapping("/contact")
    public String submitContactForm(@ModelAttribute("message") Message message, RedirectAttributes redirectAttributes) {
        messageService.saveMessage(message);
        redirectAttributes.addFlashAttribute("successMessage", "Votre message a bien été envoyé !");
        return "redirect:/";
    }
}
