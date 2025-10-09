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

import com.docker.service.VideoService;

@Controller
@RequestMapping("/admin")
public class AdminVideoController {

	private final VideoService videoService;

	public AdminVideoController(VideoService videoService) {
		this.videoService = videoService;
	}

	@GetMapping("/videos")
	public String showVideoManagementPage(Model model) {
		model.addAttribute("videos", videoService.getAllVideos());
		return "admin/video-management";
	}

	@PostMapping("/videos/add-embed")
	public String addEmbedVideo(@RequestParam("title") String title, @RequestParam("embedCode") String embedCode,
			RedirectAttributes redirectAttributes) {
		videoService.saveEmbedVideo(title, embedCode);
		redirectAttributes.addFlashAttribute("successMessage", "Vidéo (embed) ajoutée avec succès !");
		return "redirect:/admin/videos";
	}

	@PostMapping("/videos/add-upload")
	public String addUploadVideo(@RequestParam("title") String title, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		try {
			videoService.saveUploadedVideo(title, file);
			redirectAttributes.addFlashAttribute("successMessage", "Vidéo (fichier) ajoutée avec succès !");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Erreur lors de l'envoi du fichier : " + e.getMessage());
		}
		return "redirect:/admin/videos";
	}

	@PostMapping("/videos/delete/{id}")
	public String deleteVideo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			videoService.deleteVideo(id);
			redirectAttributes.addFlashAttribute("successMessage", "Vidéo supprimée avec succès !");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de la vidéo.");
		}
		return "redirect:/admin/videos";
	}
}
