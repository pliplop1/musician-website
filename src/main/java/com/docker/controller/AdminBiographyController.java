package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.docker.service.BiographyService;

@Controller
@RequestMapping("/admin")
public class AdminBiographyController {

	private final BiographyService biographyService;

	public AdminBiographyController(BiographyService biographyService) {
		this.biographyService = biographyService;
	}

	@GetMapping("/biographie")
	public String showBiographyManagementPage(Model model) {
		model.addAttribute("biography", biographyService.getBiography());
		return "admin/biography-management";
	}

	@PostMapping("/biography/update")
	public String updateBiography(@RequestParam("content") String content, RedirectAttributes redirectAttributes) {
		biographyService.saveBiography(content);
		redirectAttributes.addFlashAttribute("successMessage", "La biographie a été mise à jour avec succès !");
		return "redirect:/admin/biographie";
	}
}