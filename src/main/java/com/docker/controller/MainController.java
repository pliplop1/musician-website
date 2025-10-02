package com.docker.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
public class MainController {

    private final ConcertService concertService;
    private final MessageService messageService;
    private final UserService userService;

    public MainController(ConcertService concertService, MessageService messageService, UserService userService) {
        this.concertService = concertService;
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("concerts", concertService.findAllConcerts());
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", new Message());
        }
        
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            Set<Long> favoriteConcertIds = user.getFavoriteConcerts().stream()
                                               .map(Concert::getId)
                                               .collect(Collectors.toSet());
            model.addAttribute("favoriteConcertIds", favoriteConcertIds);
        } else {
            model.addAttribute("favoriteConcertIds", Collections.emptySet());
        }
        
        return "index";
    }

    @PostMapping("/contact")
    public String submitContactForm(@ModelAttribute Message message, RedirectAttributes redirectAttributes) {
        messageService.saveMessage(message);
        redirectAttributes.addFlashAttribute("successMessage", "Votre message a bien été envoyé !");
        return "redirect:/";
    }
}

