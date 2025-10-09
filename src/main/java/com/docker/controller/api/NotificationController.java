package com.docker.controller.api;

import com.docker.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Récupère les statistiques de notifications pour l'admin
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getNotificationStats() {
        return ResponseEntity.ok(notificationService.getNotificationStats());
    }
}
