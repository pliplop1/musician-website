package com.docker.controller.api;

import com.docker.entity.SocialLink;
import com.docker.repository.SocialLinkRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social-links")
@PreAuthorize("hasRole('ADMIN')")
public class SocialLinkOrderController {

    private final SocialLinkRepository socialLinkRepository;

    public SocialLinkOrderController(SocialLinkRepository socialLinkRepository) {
        this.socialLinkRepository = socialLinkRepository;
    }

    @PostMapping("/reorder")
    public ResponseEntity<Map<String, String>> reorderSocialLinks(@RequestBody List<Long> linkIds) {
        try {
            for (int i = 0; i < linkIds.size(); i++) {
                Long linkId = linkIds.get(i);
                SocialLink link = socialLinkRepository.findById(linkId).orElse(null);
                if (link != null) {
                    link.setDisplayOrder(i);
                    socialLinkRepository.save(link);
                }
            }
            return ResponseEntity.ok(Map.of("message", "Ordre mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors de la mise à jour de l'ordre"));
        }
    }
}
