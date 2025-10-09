package com.docker.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	/**
	 * Supprimer plusieurs messages en masse
	 */
	@PostMapping("/messages/bulk-delete")
	public String bulkDeleteMessages(@RequestParam("messageIds") List<Long> messageIds, RedirectAttributes redirectAttributes) {
		if (messageIds == null || messageIds.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Aucun message sélectionné");
			return "redirect:/admin/messages";
		}

		int count = 0;
		for (Long id : messageIds) {
			messageService.deleteMessage(id);
			count++;
		}

		redirectAttributes.addFlashAttribute("successMessage", count + " message(s) supprimé(s) avec succès !");
		return "redirect:/admin/messages";
	}

	/**
	 * Basculer l'état lu/non lu d'un message
	 */
	@PostMapping("/messages/toggle-read/{id}")
	public String toggleReadStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		messageService.toggleReadStatus(id);
		return "redirect:/admin/messages";
	}

	/**
	 * Marquer tous les messages comme lus
	 */
	@PostMapping("/messages/mark-all-read")
	public String markAllMessagesAsRead(RedirectAttributes redirectAttributes) {
		messageService.markAllAsRead();
		redirectAttributes.addFlashAttribute("successMessage", "Tous les messages ont été marqués comme lus !");
		return "redirect:/admin/messages";
	}
}