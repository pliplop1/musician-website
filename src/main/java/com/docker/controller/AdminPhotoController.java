package com.docker.controller;

import java.io.IOException;

// AJOUTEZ CET IMPORT
import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.service.PhotoService;

@Controller
@RequestMapping("/admin")
public class AdminPhotoController {

    private final PhotoService photoService;

    public AdminPhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/photos")
    public String showPhotoManagementPage(Model model) { // <-- Java sait maintenant ce qu'est "Model"
        model.addAttribute("photos", photoService.getAllPhotos());
        return "admin/photo-management";
    }

    @PostMapping("/photos/upload")
    public String uploadPhoto(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "title", required = false) String title,
                              @RequestParam(value = "description", required = false) String description,
                              @RequestParam(value = "tags", required = false) String tags,
                              @RequestParam(value = "category", required = false) String category,
                              RedirectAttributes redirectAttributes) {
        try {
            photoService.savePhoto(file, title, description, tags, category);
            redirectAttributes.addFlashAttribute("successMessage", "Photo ajoutée avec succès !");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de l'envoi de la photo : " + e.getMessage());
        }
        return "redirect:/admin/photos";
    }

    @PostMapping("/photos/edit/{id}")
    public String editPhoto(@PathVariable Long id,
                            @RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "tags", required = false) String tags,
                            @RequestParam(value = "category", required = false) String category,
                            RedirectAttributes redirectAttributes) {
        try {
            photoService.updatePhotoMetadata(id, title, description, tags, category);
            redirectAttributes.addFlashAttribute("successMessage", "Photo mise à jour avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin/photos";
    }

    @PostMapping("/photos/delete/{id}")
    public String deletePhoto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            photoService.deletePhoto(id);
            redirectAttributes.addFlashAttribute("successMessage", "Photo supprimée avec succès !");
        } catch (IOException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/admin/photos";
    }
}