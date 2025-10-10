package com.docker.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.docker.entity.User;
import com.docker.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.docker.controller")
public class UserControllerAdvice {

    private final UserService userService;

    public UserControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpServletRequest request) {
        // Ne pas charger currentUser si on est dans une page d'erreur
        // Cela évite la boucle infinie : erreur → charge avatar → erreur → charge avatar
        String requestUri = request.getRequestURI();
        if (requestUri != null && (requestUri.startsWith("/error") || requestUri.startsWith("/uploaded-"))) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            return userService.findByUsername(username);
        }

        return null;
    }
}