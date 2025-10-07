package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.docker.service.MessageService;

@Controller
@RequestMapping("/admin")
public class AdminMessageController {

	private final MessageService messageService;

	public AdminMessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@GetMapping("/messages")
	public String showMessageManagementPage(Model model) {
		model.addAttribute("messages", messageService.findAllMessages());
		return "admin/message-management";
	}

	@PostMapping("/messages/delete/{id}")
	public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		messageService.deleteMessage(id);
		redirectAttributes.addFlashAttribute("successMessage", "Le message a été supprimé avec succès !");
		return "redirect:/admin/messages";

	}
}