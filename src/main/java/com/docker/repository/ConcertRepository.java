package com.docker.repository;

import com.docker.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface qui gère les opérations de base de données pour l'entité Concert.
 * Spring Data JPA fournit les méthodes CRUD automatiquement.
 */
@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
