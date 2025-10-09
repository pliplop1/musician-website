package com.docker.service;

import com.docker.entity.SocialLink;
import com.docker.repository.SocialLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocialLinkService {

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    /**
     * Récupère tous les liens sociaux activés, triés par ordre d'affichage.
     */
    public List<SocialLink> getEnabledSocialLinks() {
        return socialLinkRepository.findByEnabledTrueOrderByDisplayOrderAsc();
    }

    /**
     * Récupère tous les liens sociaux (pour l'admin).
     */
    public List<SocialLink> getAllSocialLinks() {
        return socialLinkRepository.findAllByOrderByDisplayOrderAsc();
    }

    /**
     * Récupère un lien social par son ID.
     */
    public SocialLink getSocialLinkById(Long id) {
        return socialLinkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lien social introuvable avec l'ID : " + id));
    }

    /**
     * Sauvegarde ou met à jour un lien social.
     */
    @Transactional
    public SocialLink saveSocialLink(SocialLink socialLink) {
        // Si l'URL est vide ou null, on désactive le lien
        if (socialLink.getUrl() == null || socialLink.getUrl().trim().isEmpty()) {
            socialLink.setEnabled(false);
        }
        return socialLinkRepository.save(socialLink);
    }

    /**
     * Met à jour un lien social existant.
     */
    @Transactional
    public void updateSocialLink(Long id, String url, Boolean enabled) {
        SocialLink socialLink = getSocialLinkById(id);
        socialLink.setUrl(url != null ? url.trim() : "");

        // Si l'URL est vide, on désactive automatiquement
        if (socialLink.getUrl().isEmpty()) {
            socialLink.setEnabled(false);
        } else {
            socialLink.setEnabled(enabled != null ? enabled : true);
        }

        socialLinkRepository.save(socialLink);
    }

    /**
     * Supprime un lien social.
     */
    @Transactional
    public void deleteSocialLink(Long id) {
        socialLinkRepository.deleteById(id);
    }
}
