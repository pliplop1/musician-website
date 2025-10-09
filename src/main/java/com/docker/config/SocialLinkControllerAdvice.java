package com.docker.config;

import com.docker.service.SocialLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import com.docker.entity.SocialLink;

/**
 * Injecte les liens sociaux actifs dans tous les modèles de templates.
 */
@ControllerAdvice
public class SocialLinkControllerAdvice {

    @Autowired
    private SocialLinkService socialLinkService;

    /**
     * Ajoute la liste des liens sociaux actifs à tous les modèles.
     */
    @ModelAttribute("enabledSocialLinks")
    public List<SocialLink> getEnabledSocialLinks() {
        return socialLinkService.getEnabledSocialLinks();
    }
}
