package com.docker.controller;

import com.docker.entity.PasswordResetToken;
import com.docker.service.PasswordResetService;
import com.docker.service.PasswordValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Contrôleur pour la réinitialisation de mot de passe
 */
@Controller
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    private final PasswordResetService passwordResetService;
    private final PasswordValidationService passwordValidationService;

    public PasswordResetController(PasswordResetService passwordResetService, PasswordValidationService passwordValidationService) {
        this.passwordResetService = passwordResetService;
        this.passwordValidationService = passwordValidationService;
    }

    /**
     * Affiche le formulaire "Mot de passe oublié"
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    /**
     * Traite la demande de réinitialisation
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes) {
        logger.info("Demande de réinitialisation de mot de passe pour l'email: {}", email);

        boolean success = passwordResetService.createPasswordResetTokenForUser(email);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                "Si votre email existe dans notre système, vous recevrez un lien de réinitialisation.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Une erreur s'est produite. Veuillez réessayer.");
        }

        return "redirect:/forgot-password";
    }

    /**
     * Affiche le formulaire de réinitialisation avec token
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> resetToken = passwordResetService.validatePasswordResetToken(token);

        if (resetToken.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Le lien de réinitialisation est invalide ou a expiré.");
            return "redirect:/forgot-password";
        }

        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    /**
     * Traite le changement de mot de passe
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        // Validation du mot de passe avec le service de validation
        String validationError = passwordValidationService.getValidationErrorMessage(password);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("errorMessage", validationError);
            redirectAttributes.addAttribute("token", token);
            return "redirect:/reset-password";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Les mots de passe ne correspondent pas.");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/reset-password";
        }

        // Réinitialisation
        boolean success = passwordResetService.resetPassword(token, password);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                "Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Le lien de réinitialisation est invalide ou a expiré.");
            return "redirect:/forgot-password";
        }
    }
}
