package com.docker.controller;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.Concert;
import com.docker.entity.Role;
import com.docker.entity.User;
import com.docker.repository.RoleRepository;
import com.docker.service.ConcertService;
import com.docker.service.MessageService;
import com.docker.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ConcertService concertService;
    private final MessageService messageService;
    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(ConcertService concertService, MessageService messageService, UserService userService, RoleRepository roleRepository) {
        this.concertService = concertService;
        this.messageService = messageService;
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("concerts", concertService.findAllConcerts());
        model.addAttribute("messages", messageService.findAllMessages());
        if (!model.containsAttribute("concert")) {
            model.addAttribute("concert", new Concert());
        }
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String showUserManagement(Model model, 
                                     @RequestParam(defaultValue = "username") String sortField,
                                     @RequestParam(defaultValue = "asc") String sortDir,
                                     @RequestParam(required = false) String keyword) {
        
        List<User> users = userService.searchUsers(keyword);
        
        Comparator<User> comparator = Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER);
        if ("roles".equals(sortField)) {
            comparator = Comparator.comparing(user -> user.getRoles().stream()
                                                            .map(Role::getName)
                                                            .sorted()
                                                            .findFirst()
                                                            .orElse(""));
        }
        
        if ("desc".equals(sortDir)) {
            comparator = comparator.reversed();
        }
        users.sort(comparator);

        model.addAttribute("users", users);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        
        return "admin/user-management";
    }

    @GetMapping("/users/edit/{id}")
    public String showUserEditForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin/edit-user";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user, @RequestParam(name = "roleId", required = false) Long roleId, RedirectAttributes redirectAttributes) {
        Set<Role> newRoles = new HashSet<>();
        if (roleId != null) {
            roleRepository.findById(roleId).ifPresent(newRoles::add);
        }
        
        userService.updateUserFromAdmin(user.getId(), user.getUsername(), user.getEmail(), newRoles);
        
        redirectAttributes.addFlashAttribute("successMessage", "L'utilisateur a été mis à jour avec succès.");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "L'utilisateur a été supprimé avec succès !");
        return "redirect:/admin/users";
    }
    
    // ... (autres méthodes pour les concerts et messages)
    
    @PostMapping("/concerts/add")
    public String saveConcert(@ModelAttribute Concert concert, RedirectAttributes redirectAttributes) {
        concertService.saveConcert(concert);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été ajouté avec succès !");
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/concerts/delete/{id}")
    public String deleteConcert(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        concertService.deleteConcert(id);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été supprimé avec succès !");
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        messageService.deleteMessage(id);
        redirectAttributes.addFlashAttribute("successMessage", "Le message a été supprimé avec succès !");
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/concerts/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Concert concert = concertService.findConcertById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));
        model.addAttribute("concert", concert);
        return "admin/edit-concert";
    }

    @PostMapping("/concerts/update/{id}")
    public String updateConcert(@PathVariable Long id, @ModelAttribute Concert concert, RedirectAttributes redirectAttributes) {
        Concert existingConcert = concertService.findConcertById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));
        existingConcert.setLocation(concert.getLocation());
        existingConcert.setDate(concert.getDate());
        existingConcert.setDescription(concert.getDescription());
        
        concertService.saveConcert(existingConcert);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été modifié avec succès !");
        return "redirect:/admin/dashboard";
    }
}

