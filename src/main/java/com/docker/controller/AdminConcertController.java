// src/main/java/com/docker/controller/AdminConcertController.java
package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.Concert;
import com.docker.service.ConcertService;

@Controller
@RequestMapping("/admin") // Toutes les routes ici commenceront par /admin
public class AdminConcertController {

    private final ConcertService concertService;

    // Le constructeur n'injecte QUE le service nécessaire
    public AdminConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @PostMapping("/concerts/add")
    public String saveConcert(@ModelAttribute Concert concert, RedirectAttributes redirectAttributes) {
        concertService.saveConcert(concert);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été ajouté avec succès !");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/concerts/delete/{id}")
    public String deleteConcert(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        concertService.deleteConcert(id);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été supprimé avec succès !");
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
    public String updateConcert(@PathVariable Long id, @ModelAttribute Concert concert,
            RedirectAttributes redirectAttributes) {
        Concert existingConcert = concertService.findConcertById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid concert Id:" + id));
        existingConcert.setLocation(concert.getLocation());
        existingConcert.setDate(concert.getDate());
        existingConcert.setDescription(concert.getDescription());
        concertService.saveConcert(existingConcert);
        redirectAttributes.addFlashAttribute("successMessage", "Le concert a été modifié avec succès !");
        return "redirect:/admin/dashboard";
    }
 // AJOUT : Méthode pour afficher la page de gestion des concerts
    @GetMapping("/concerts")
    public String showConcertManagementPage(Model model) {
        model.addAttribute("concerts", concertService.findAllConcerts());
        if (!model.containsAttribute("concert")) {
            model.addAttribute("concert", new Concert());
        }
        return "admin/concert-management";
    }
}