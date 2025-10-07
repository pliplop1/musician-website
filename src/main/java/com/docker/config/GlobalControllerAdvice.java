package com.docker.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

// MODIFICATION FINALE : On cible le package spécifique des contrôleurs de vues.
@ControllerAdvice(basePackages = "com.docker.controller")
public class GlobalControllerAdvice {

    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}