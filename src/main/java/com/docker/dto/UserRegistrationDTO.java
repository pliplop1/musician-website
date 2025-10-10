package com.docker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'inscription d'un utilisateur depuis Vue.js
 * Contient les validations nécessaires
 */
public record UserRegistrationDTO(

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    String username,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    String password
) {
}
