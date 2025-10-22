package com.docker.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.LoginAttempt;
import com.docker.entity.User;
import com.docker.service.UserService;
import com.docker.service.BadgeService;
import com.docker.service.DataExportService;
import com.docker.service.LoginAttemptService;
import com.docker.repository.TrackRepository;
import com.docker.repository.VideoRepository;
import com.docker.repository.PhotoRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final BadgeService badgeService;
    private final DataExportService dataExportService;
    private final LoginAttemptService loginAttemptService;
    private final TrackRepository trackRepository;
    private final VideoRepository videoRepository;
    private final PhotoRepository photoRepository;

    public UserController(UserService userService, BadgeService badgeService, DataExportService dataExportService,
                         LoginAttemptService loginAttemptService, TrackRepository trackRepository,
                         VideoRepository videoRepository, PhotoRepository photoRepository) {
        this.userService = userService;
        this.badgeService = badgeService;
        this.dataExportService = dataExportService;
        this.loginAttemptService = loginAttemptService;
        this.trackRepository = trackRepository;
        this.videoRepository = videoRepository;
        this.photoRepository = photoRepository;
    }

    @GetMapping("/profile")
    @Transactional
    public String showProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        // Calcul du nombre de jours depuis l'inscription
        long daysSinceRegistration = 0;
        if (user.getCreatedAt() != null) {
            daysSinceRegistration = ChronoUnit.DAYS.between(
                user.getCreatedAt().toLocalDate(),
                java.time.LocalDate.now()
            );
        }

        // Récupérer les badges de l'utilisateur
        var userBadges = badgeService.getUserBadges(user);
        var badgeCount = badgeService.countUserBadges(user);

        // Récupérer les informations de dernière connexion
        LoginAttempt lastLogin = loginAttemptService.getLastSuccessfulLogin(username);

        // Récupérer les contenus likés par l'utilisateur
        var likedTracks = trackRepository.findLikedByUser(user);
        var likedVideos = videoRepository.findLikedByUser(user);
        var likedPhotos = photoRepository.findLikedByUser(user);

        // Calcul du pourcentage de complétion du profil
        int profileCompletion = calculateProfileCompletion(user);

        // Forcer l'initialisation de la collection lazy favoriteConcerts
        user.getFavoriteConcerts().size(); // Cela force Hibernate à charger les données

        model.addAttribute("username", username);
        model.addAttribute("user", user);
        model.addAttribute("favoriteConcerts", user.getFavoriteConcerts());
        model.addAttribute("daysSinceRegistration", daysSinceRegistration);
        model.addAttribute("userBadges", userBadges);
        model.addAttribute("badgeCount", badgeCount);
        model.addAttribute("lastLogin", lastLogin);
        model.addAttribute("likedTracks", likedTracks);
        model.addAttribute("likedVideos", likedVideos);
        model.addAttribute("likedPhotos", likedPhotos);
        model.addAttribute("profileCompletion", profileCompletion);

        return "user/profile";
    }

    /**
     * Calcule le pourcentage de complétion du profil utilisateur
     */
    private int calculateProfileCompletion(User user) {
        int score = 0;
        int total = 6;

        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) score++;
        if (user.getLastName() != null && !user.getLastName().isEmpty()) score++;
        if (user.getBio() != null && !user.getBio().isEmpty()) score++;
        if (user.getAvatarFilename() != null) score++;
        if (user.getEmail() != null && !user.getEmail().isEmpty()) score++; // Toujours vrai normalement
        if (user.getUsername() != null && !user.getUsername().isEmpty()) score++; // Toujours vrai normalement

        return (score * 100) / total;
    }

    @GetMapping("/login-history")
    public String showLoginHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "attemptTime") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            Model model,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        // Créer le Pageable avec tri
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // Récupérer l'historique des connexions paginé (30 derniers jours)
        Page<LoginAttempt> loginHistoryPage = loginAttemptService.getLoginHistoryPaginated(username, 30, pageable);

        model.addAttribute("user", user);
        model.addAttribute("loginHistoryPage", loginHistoryPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", loginHistoryPage.getTotalPages());
        model.addAttribute("totalItems", loginHistoryPage.getTotalElements());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        model.addAttribute("size", size);

        return "user/login-history";
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
    
    // ============================================
    // GESTION DE LA PHOTO DE PROFIL ET BIO
    // ============================================
    
    @GetMapping("/profile/details")
    public String showProfileDetailsForm(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user/edit-profile-details";
    }
    
    @PostMapping("/profile/details")
    public String updateProfileDetails(@RequestParam(required = false) String firstName,
                                      @RequestParam(required = false) String lastName,
                                      @RequestParam(required = false) String bio,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfileDetails(principal.getName(), firstName, lastName, bio);
            redirectAttributes.addFlashAttribute("successMessage", "Vos informations ont été mises à jour avec succès !");
            return "redirect:/user/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
            return "redirect:/user/profile/details";
        }
    }
    
    @PostMapping("/profile/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.updateAvatar(principal.getName(), file);
            redirectAttributes.addFlashAttribute("successMessage", "Photo de profil mise à jour avec succès !");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'upload : " + e.getMessage());
        }
        return "redirect:/user/profile";
    }
    
    @PostMapping("/profile/delete-avatar")
    public String deleteAvatar(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteAvatar(principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Photo de profil supprimée avec succès !");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    // ============================================
    // RGPD - EXPORT ET SUPPRESSION DES DONNÉES
    // ============================================

    /**
     * Télécharge toutes les données personnelles de l'utilisateur (RGPD - Article 20)
     * Format JSON structuré et lisible
     */
    @GetMapping("/export-data")
    public ResponseEntity<String> exportUserData(Principal principal) {
        try {
            String username = principal.getName();
            String jsonData = dataExportService.exportUserDataByUsername(username);
            String filename = dataExportService.generateExportFilename(username);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return ResponseEntity.ok()
                .headers(headers)
                .body(jsonData);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("{\"error\": \"Erreur lors de l'export des données : " + e.getMessage() + "\"}");
        }
    }

    /**
     * Affiche la page de confirmation de suppression de compte
     */
    @GetMapping("/delete-account")
    public String showDeleteAccountPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user/delete-account";
    }

    /**
     * Supprime définitivement le compte utilisateur (RGPD - Article 17 - Droit à l'oubli)
     * Après confirmation, le compte et toutes les données associées sont supprimés
     */
    @PostMapping("/delete-account")
    public String deleteAccount(@RequestParam String confirmPassword,
                               Principal principal,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username);

            // Vérifier que le mot de passe de confirmation est correct
            if (!userService.verifyPassword(user, confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Mot de passe incorrect. Suppression annulée.");
                return "redirect:/user/delete-account";
            }

            // Déconnecter l'utilisateur avant la suppression
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }

            // Supprimer le compte et toutes les données associées (RGPD)
            userService.deleteUserAccountCompletely(user.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                "Votre compte a été supprimé définitivement. Nous espérons vous revoir bientôt.");
            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Erreur lors de la suppression du compte : " + e.getMessage());
            return "redirect:/user/delete-account";
        }
    }
}