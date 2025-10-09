package com.docker.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.RoleRepository;
import com.docker.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminUserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public String showUserManagement(Model model, @RequestParam(defaultValue = "username") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir, @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField));
        Page<User> userPage = userService.searchUsers(keyword, pageable);
        model.addAttribute("userPage", userPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        return "admin/user-management";
    }

    @GetMapping("/users/edit/{id}")
    public String showUserEditForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/edit-user";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user, @RequestParam(name = "roleId", required = false) Long roleId,
            RedirectAttributes redirectAttributes) {
        Set<Role> roles = new HashSet<>();
        if (roleId != null) {
            roleRepository.findById(roleId).ifPresent(roles::add);
        }

        if (roles.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Veuillez sélectionner au moins un rôle.");
            return "redirect:/admin/users/edit/" + user.getId();
        }

        userService.updateUserFromAdmin(user.getId(), user.getUsername(), user.getEmail(), roles);
        redirectAttributes.addFlashAttribute("successMessage", "L'utilisateur a été mis à jour avec succès.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "L'utilisateur a été supprimé avec succès !");
        return "redirect:/admin/users";
    }

    /**
     * Supprimer plusieurs utilisateurs en masse
     */
    @PostMapping("/users/bulk-delete")
    public String bulkDeleteUsers(@RequestParam("userIds") List<Long> userIds, RedirectAttributes redirectAttributes) {
        if (userIds == null || userIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aucun utilisateur sélectionné");
            return "redirect:/admin/users";
        }

        int count = 0;
        for (Long id : userIds) {
            userService.deleteUser(id);
            count++;
        }

        redirectAttributes.addFlashAttribute("successMessage", count + " utilisateur(s) supprimé(s) avec succès !");
        return "redirect:/admin/users";
    }
}