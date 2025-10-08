package com.docker.repository;

import com.docker.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface qui gère les opérations de base de données pour l'entité Concert.
 * Spring Data JPA fournit les méthodes CRUD automatiquement.
 */
@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {

    /**
     * Trouve les concerts qui ont lieu à une date spécifique et qui ont des utilisateurs intéressés.
     * Utilisé pour envoyer des notifications 24h avant le concert.
     */
    @Query("SELECT DISTINCT c FROM Concert c JOIN FETCH c.interestedUsers WHERE c.date = :date AND SIZE(c.interestedUsers) > 0")
    List<Concert> findConcertsWithInterestedUsersForDate(@Param("date") LocalDate date);
}
