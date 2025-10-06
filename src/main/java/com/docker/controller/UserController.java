package com.docker.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.User;
import com.docker.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("favoriteConcerts", user.getFavoriteConcerts());
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user/edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(principal.getName(), user.getUsername(), user.getEmail());
            
            // Rafraîchir la session de sécurité avec les nouvelles informations
            User updatedUser = userService.findByUsername(user.getUsername());
            List<GrantedAuthority> authorities = new ArrayList<>();
            updatedUser.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
            
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUser.getUsername(), updatedUser.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            
            redirectAttributes.addFlashAttribute("successMessage", "Votre profil a été mis à jour avec succès !");
            return "redirect:/user/profile"; 
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/profile/edit";
        }
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "user/change-password";
    }

    @PostMapping("/profile/change-password")
    public String handleChangePassword(Principal principal, RedirectAttributes redirectAttributes,
                                       @RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.changeUserPassword(principal.getName(), oldPassword, newPassword, confirmPassword);

            // La déconnexion est une bonne pratique de sécurité après un changement de mot de passe
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Votre mot de passe a été changé avec succès. Veuillez vous reconnecter.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/profile/change-password";
        }
    }

    @PostMapping("/favorites/add/{concertId}")
    public String addFavorite(@PathVariable Long concertId, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        userService.addFavoriteConcert(principal.getName(), concertId);
        redirectAttributes.addFlashAttribute("successMessage", "Concert ajouté aux favoris !");
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/favorites/remove/{concertId}")
    public String removeFavorite(@PathVariable Long concertId, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        userService.removeFavoriteConcert(principal.getName(), concertId);
        redirectAttributes.addFlashAttribute("successMessage", "Concert retiré des favoris.");
        return "redirect:" + request.getHeader("Referer");
    }
}