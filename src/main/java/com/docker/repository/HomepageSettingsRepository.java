package com.docker.repository;

import com.docker.entity.HomepageSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour gérer les paramètres de la page d'accueil
 * Il n'y aura qu'une seule instance de HomepageSettings dans la base
 */
@Repository
public interface HomepageSettingsRepository extends JpaRepository<HomepageSettings, Long> {

    /**
     * Récupère les paramètres de la page d'accueil
     * Comme il n'y a qu'une seule entrée, on récupère la première
     * @return Optional contenant les paramètres si présents
     */
    Optional<HomepageSettings> findFirstByOrderByIdAsc();
}
