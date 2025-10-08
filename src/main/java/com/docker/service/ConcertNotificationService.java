package com.docker.service;

import com.docker.entity.Concert;
import com.docker.entity.User;
import com.docker.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service pour gérer les notifications des concerts favoris.
 * Envoie automatiquement des emails 24h avant chaque concert.
 */
@Service
@Slf4j
public class ConcertNotificationService {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private EmailService emailService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Méthode exécutée automatiquement chaque jour à 9h00.
     * Vérifie les concerts qui auront lieu demain et envoie des notifications.
     */
    @Scheduled(cron = "0 0 9 * * ?") // Tous les jours à 9h00
    public void sendDailyConcertReminders() {
        log.info("Démarrage de la vérification des concerts pour les notifications...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Concert> upcomingConcerts = concertRepository.findConcertsWithInterestedUsersForDate(tomorrow);

        if (upcomingConcerts.isEmpty()) {
            log.info("Aucun concert prévu pour demain avec des utilisateurs intéressés.");
            return;
        }

        log.info("Nombre de concerts trouvés pour demain : {}", upcomingConcerts.size());

        int emailsSent = 0;
        for (Concert concert : upcomingConcerts) {
            emailsSent += sendNotificationsForConcert(concert);
        }

        log.info("Notifications terminées. Total d'emails envoyés : {}", emailsSent);
    }

    /**
     * Envoie des notifications pour un concert spécifique à tous les utilisateurs intéressés.
     *
     * @param concert Le concert pour lequel envoyer les notifications
     * @return Le nombre d'emails envoyés
     */
    private int sendNotificationsForConcert(Concert concert) {
        int count = 0;
        String concertInfo = concert.getLocation();
        if (concert.getDescription() != null && !concert.getDescription().isEmpty()) {
            concertInfo += " - " + concert.getDescription();
        }
        String concertDate = concert.getDate().format(DATE_FORMATTER);

        for (User user : concert.getInterestedUsers()) {
            try {
                String userName = user.getFullName();
                emailService.sendConcertNotification(
                    user.getEmail(),
                    userName,
                    concertInfo,
                    concertDate
                );
                count++;
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de la notification à l'utilisateur {} : {}",
                    user.getEmail(), e.getMessage());
            }
        }

        log.info("Notifications envoyées pour le concert '{}' : {} emails", concertInfo, count);
        return count;
    }

    /**
     * Méthode manuelle pour tester l'envoi de notifications.
     * Peut être appelée depuis un contrôleur admin pour tester le système.
     */
    public void sendTestNotifications() {
        log.info("Test manuel des notifications de concerts...");
        sendDailyConcertReminders();
    }
}
