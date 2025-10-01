package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;

/**
 * Contrôleur pour la section des utilisateurs authentifiés.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * Affiche la page de profil de l'utilisateur.
     * @param model Le modèle pour passer des données à la vue.
     * @param principal L'objet représentant l'utilisateur actuellement connecté.
     * @return Le nom de la vue à afficher.
     */
    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        // Ajoute le nom de l'utilisateur au modèle pour l'afficher sur la page
        model.addAttribute("username", principal.getName());
        return "user/profile";
    }
}