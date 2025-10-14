package com.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour les pages publiques (accessibles sans authentification)
 * Note: Les pages légales (privacy-policy, mentions-legales, cookies) sont gérées par LegalController
 */
@Controller
public class PublicPagesController {

    // Les routes légales sont maintenant dans LegalController
    // Ce contrôleur peut être utilisé pour d'autres pages publiques futures
}
