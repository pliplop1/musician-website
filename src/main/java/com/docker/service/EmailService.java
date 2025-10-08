package com.docker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * Service pour l'envoi d'emails.
 */
@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String fromEmail;

    // Mode simulation : true = simule l'envoi, false = envoie vraiment
    @Value("${email.simulation.mode:true}")
    private boolean simulationMode;

    /**
     * Envoie un email simple.
     *
     * @param to      Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param text    Corps du message
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        // MODE SIMULATION
        if (simulationMode) {
            log.info("📧 [MODE SIMULATION] Email qui serait envoyé :");
            log.info("   De      : {}", fromEmail);
            log.info("   À       : {}", to);
            log.info("   Sujet   : {}", subject);
            log.info("   Message : {}", text.substring(0, Math.min(100, text.length())) + "...");
            log.info("✅ [SIMULATION] Email simulé avec succès pour : {}", to);
            return;
        }

        // MODE RÉEL
        try {
            if (mailSender == null) {
                log.error("❌ JavaMailSender non configuré ! Activez le mode simulation ou configurez SMTP.");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("✅ Email envoyé avec succès à : {}", to);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email à {} : {}", to, e.getMessage());

            // Message d'aide pour les erreurs courantes
            if (e.getMessage().contains("Authentication failed")) {
                log.error("⚠️  AIDE : Pour Hotmail/Outlook, vous devez utiliser un MOT DE PASSE D'APPLICATION");
                log.error("⚠️  Consultez le fichier CONFIGURATION_EMAIL_HOTMAIL.md pour les instructions");
                log.error("⚠️  Lien : https://account.microsoft.com/security");
            } else if (e.getMessage().contains("Could not connect")) {
                log.error("⚠️  AIDE : Vérifiez votre connexion internet et les paramètres du pare-feu");
            }
        }
    }

    /**
     * Envoie un email de notification de concert.
     *
     * @param to           Adresse email du destinataire
     * @param userName     Nom de l'utilisateur
     * @param concertInfo  Informations sur le concert
     * @param concertDate  Date du concert
     */
    public void sendConcertNotification(String to, String userName, String concertInfo, String concertDate) {
        String subject = "🎵 Rappel : Concert demain !";

        String text = String.format(
            "Bonjour %s,\n\n" +
            "Nous vous rappelons que le concert suivant aura lieu demain :\n\n" +
            "📍 Lieu : %s\n" +
            "📅 Date : %s\n\n" +
            "Ne manquez pas cet événement !\n\n" +
            "À très bientôt,\n" +
            "L'équipe Musician Website",
            userName,
            concertInfo,
            concertDate
        );

        sendSimpleEmail(to, subject, text);
    }
}
