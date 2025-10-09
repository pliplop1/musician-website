package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.User;
import com.docker.service.UserService;
import jakarta.validation.Valid;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // Vérifier les erreurs de validation Bean Validation
        if (result.hasErrors()) {
            return "register";
        }

        // On utilise un bloc try-catch pour gérer les erreurs de validation
        try {
            if (userService.findByUsername(user.getUsername()) != null) {
                model.addAttribute("errorMessage", "Ce nom d'utilisateur existe déjà.");
                return "register";
            }
            if (userService.findByEmail(user.getEmail()) != null) {
                model.addAttribute("errorMessage", "Cet email est déjà utilisé.");
                return "register";
            }

            userService.saveUser(user);

            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Ici, on récupère le message d'erreur détaillé du service (pour le mot de passe)
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}