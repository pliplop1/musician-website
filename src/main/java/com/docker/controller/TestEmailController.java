package com.docker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.docker.service.EmailService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-email")
    public String testEmail(@RequestParam(defaultValue = "user25102025@hotmail.com") String to) {
        try {
            log.info("🧪 Test d'envoi d'email vers : {}", to);

            emailService.sendSimpleEmail(
                to,
                "Test Email - Musician Website",
                "Ceci est un email de test envoyé depuis Musician Website.\n\n" +
                "Si vous recevez cet email, la configuration SMTP fonctionne correctement !\n\n" +
                "Date: " + java.time.LocalDateTime.now()
            );

            return "✅ Email de test envoyé avec succès à : " + to + "\n" +
                   "Vérifiez votre boîte de réception (et le dossier spam)";
        } catch (Exception e) {
            log.error("❌ Erreur lors du test d'envoi : ", e);
            return "❌ Erreur : " + e.getMessage();
        }
    }
}
