package com.docker.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docker.entity.User;
import com.docker.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API REST pour obtenir les informations de l'utilisateur connecté
 * Utilisé par le frontend Vue.js pour vérifier l'authentification
 */
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère les informations de l'utilisateur actuellement connecté
     * Retourne 404 si non connecté, ou un objet JSON avec les infos utilisateur
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'utilisateur est authentifié (et pas anonyme)
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(404).build();
        }

        // Construire la réponse avec les informations utilisateur
        Map<String, Object> response = new HashMap<>();

        // Username
        String username;
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails =
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        } else {
            username = authentication.getName();
        }
        response.put("username", username);

        // Récupérer l'utilisateur pour avoir l'avatar
        User user = userService.findByUsername(username);
        if (user != null && user.getAvatarFilename() != null && !user.getAvatarFilename().isEmpty()) {
            response.put("avatarUrl", "/uploaded-avatars/" + user.getAvatarFilename());
        }

        // Roles
        response.put("roles", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

        // Vérifier si admin
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SUPERADMIN"));
        response.put("isAdmin", isAdmin);

        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }
}
