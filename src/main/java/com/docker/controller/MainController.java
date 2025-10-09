package com.docker.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docker.entity.Concert;
import com.docker.entity.Message;
import com.docker.entity.User;
import com.docker.service.ConcertService;
import com.docker.service.MessageService;
import com.docker.service.UserService;
import com.docker.service.BiographyService;
import com.docker.service.PhotoService;
import com.docker.service.TrackService;
import com.docker.service.VideoService;
import com.docker.service.CommentService;
import com.docker.entity.CommentType;
import jakarta.validation.Valid;

@Controller
public class MainController {

	private final ConcertService concertService;
	private final MessageService messageService;
	private final UserService userService;
	private final BiographyService biographyService;
	private final PhotoService photoService;
	private final TrackService trackService;
	private final VideoService videoService;
	private final CommentService commentService;

	public MainController(ConcertService concertService, MessageService messageService, UserService userService,
			BiographyService biographyService, PhotoService photoService, TrackService trackService, VideoService videoService,
			CommentService commentService) {
		this.concertService = concertService;
		this.messageService = messageService;
		this.userService = userService;
		this.biographyService = biographyService;
		this.photoService = photoService;
		this.trackService = trackService;
		this.videoService = videoService;
		this.commentService = commentService;
	}

	@GetMapping("/")
	public String home(Model model, Principal principal) {
		var concerts = concertService.findAllConcerts();
		model.addAttribute("concerts", concerts);

		// Ajouter les compteurs et commentaires pour chaque concert
		concerts.forEach(concert -> {
			Long commentCount = commentService.countApprovedComments(CommentType.CONCERT, concert.getId());
			model.addAttribute("commentCount_" + concert.getId(), commentCount);

			var comments = commentService.getApprovedComments(CommentType.CONCERT, concert.getId());
			model.addAttribute("comments_" + concert.getId(), comments);
		});

		if (!model.containsAttribute("message")) {
			model.addAttribute("message", new Message());
		}

		if (principal != null) {
			User user = userService.findByUsername(principal.getName());
			Set<Long> favoriteConcertIds = user.getFavoriteConcerts().stream().map(Concert::getId)
					.collect(Collectors.toSet());
			model.addAttribute("favoriteConcertIds", favoriteConcertIds);
		} else {
			model.addAttribute("favoriteConcertIds", Collections.emptySet());
		}

		return "index";
	}

	@GetMapping("/biographie")
	public String showBiography(Model model) {
		model.addAttribute("biography", biographyService.getBiography());
		return "biographie"; // Le nom de notre nouvelle page HTML
	}

	@PostMapping("/contact")
	public String submitContactForm(@Valid @ModelAttribute("message") Message message, BindingResult result, Model model, RedirectAttributes redirectAttributes, Principal principal) {
		if (result.hasErrors()) {
			// Recharger les données nécessaires pour afficher la page d'accueil
			var concerts = concertService.findAllConcerts();
			model.addAttribute("concerts", concerts);

			concerts.forEach(concert -> {
				Long commentCount = commentService.countApprovedComments(CommentType.CONCERT, concert.getId());
				model.addAttribute("commentCount_" + concert.getId(), commentCount);
				var comments = commentService.getApprovedComments(CommentType.CONCERT, concert.getId());
				model.addAttribute("comments_" + concert.getId(), comments);
			});

			if (principal != null) {
				User user = userService.findByUsername(principal.getName());
				Set<Long> favoriteConcertIds = user.getFavoriteConcerts().stream().map(Concert::getId)
						.collect(Collectors.toSet());
				model.addAttribute("favoriteConcertIds", favoriteConcertIds);
			} else {
				model.addAttribute("favoriteConcertIds", Collections.emptySet());
			}

			return "index";
		}

		messageService.saveMessage(message);
		redirectAttributes.addFlashAttribute("successMessage", "Votre message a bien été envoyé ! Il sera vérifié par un administrateur.");
		return "redirect:/";
	}

	@GetMapping("/galerie")
	public String showGallery(Model model) {
		model.addAttribute("photos", photoService.getAllPhotos());
		return "galerie";
	}

	@GetMapping("/musique")
	public String showMusic(Model model) {
		model.addAttribute("tracks", trackService.getAllTracks());
		return "musique";
	}

	@GetMapping("/videos")
	public String showVideos(Model model) {
		model.addAttribute("videos", videoService.getAllVideos());
		return "videos";
	}
}
