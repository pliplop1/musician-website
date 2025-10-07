package com.docker.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.docker.service.TrackService;

@Controller
@RequestMapping("/admin")
public class AdminTrackController {

	private final TrackService trackService;

	public AdminTrackController(TrackService trackService) {
		this.trackService = trackService;
	}

	@GetMapping("/musique")
	public String showMusicManagementPage(Model model) {
		model.addAttribute("tracks", trackService.getAllTracks());
		return "admin/music-management";
	}

	@PostMapping("/tracks/add-embed")
	public String addEmbedTrack(@RequestParam("title") String title, @RequestParam("embedCode") String embedCode,
			RedirectAttributes redirectAttributes) {
		trackService.saveEmbedTrack(title, embedCode);
		redirectAttributes.addFlashAttribute("successMessage", "Piste musicale (embed) ajoutée avec succès !");
		return "redirect:/admin/musique";
	}

	@PostMapping("/tracks/add-upload")
	public String addUploadTrack(@RequestParam("title") String title, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		try {
			trackService.saveUploadedTrack(title, file);
			redirectAttributes.addFlashAttribute("successMessage", "Piste musicale (fichier) ajoutée avec succès !");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Erreur lors de l'envoi du fichier : " + e.getMessage());
		}
		return "redirect:/admin/musique";
	}

	@PostMapping("/tracks/delete/{id}")
	public String deleteTrack(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			trackService.deleteTrack(id);
			redirectAttributes.addFlashAttribute("successMessage", "Piste musicale supprimée avec succès !");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de la piste.");
		}
		return "redirect:/admin/musique";
	}
}