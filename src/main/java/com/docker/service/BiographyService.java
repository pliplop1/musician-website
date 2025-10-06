// /src/main/java/com/docker/service/BiographyService.java

package com.docker.service;

import com.docker.entity.Biography;
import com.docker.repository.BiographyRepository;
import org.springframework.stereotype.Service;

@Service
public class BiographyService {

    private final BiographyRepository biographyRepository;
    private static final Long BIOGRAPHY_ID = 1L; // ID unique pour la biographie

    public BiographyService(BiographyRepository biographyRepository) {
        this.biographyRepository = biographyRepository;
    }

    public Biography getBiography() {
        // Trouve la biographie avec l'ID 1, ou en crée une nouvelle si elle n'existe pas
        return biographyRepository.findById(BIOGRAPHY_ID).orElseGet(() -> {
            Biography newBio = new Biography();
            newBio.setId(BIOGRAPHY_ID);
            newBio.setContent("Ceci est une biographie par défaut. Modifiez-la depuis le panneau d'administration.");
            return biographyRepository.save(newBio);
        });
    }

    public void saveBiography(String content) {
        Biography bio = getBiography(); // Récupère ou crée la biographie
        bio.setContent(content);
        biographyRepository.save(bio);
    }
}