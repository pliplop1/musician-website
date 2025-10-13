package com.docker.service;

import com.docker.entity.Concert;
import com.docker.repository.ConcertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ConcertService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ConcertServiceTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Test
    void testFindAllConcerts_ReturnsAllConcerts() {
        // Given
        createAndSaveConcert("Paris", LocalDate.now().plusDays(7));
        createAndSaveConcert("Lyon", LocalDate.now().plusDays(14));

        // When
        List<Concert> concerts = concertService.findAllConcerts();

        // Then
        assertTrue(concerts.size() >= 2);
    }

    @Test
    void testFindConcertById_ExistingConcert_ReturnsConcert() {
        // Given
        Concert concert = createAndSaveConcert("Test Location", LocalDate.now().plusDays(7));

        // When
        Optional<Concert> found = concertService.findConcertById(concert.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test Location", found.get().getLocation());
    }

    @Test
    void testSaveConcert_Success() {
        // Given
        Concert concert = new Concert();
        concert.setLocation("Paris");
        concert.setDate(LocalDate.now().plusDays(30));
        concert.setDescription("Test concert");

        // When
        Concert saved = concertService.saveConcert(concert);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Paris", saved.getLocation());
    }

    @Test
    void testDeleteConcert_ExistingConcert_Success() {
        // Given
        Concert concert = createAndSaveConcert("Delete Me", LocalDate.now().plusDays(7));
        Long concertId = concert.getId();

        // When
        concertService.deleteConcert(concertId);

        // Then
        assertFalse(concertRepository.findById(concertId).isPresent());
    }

    @Test
    void testCountConcerts_ReturnsCorrectCount() {
        // Given
        long initialCount = concertService.countConcerts();
        createAndSaveConcert("Location 1", LocalDate.now().plusDays(7));
        createAndSaveConcert("Location 2", LocalDate.now().plusDays(14));

        // When
        long newCount = concertService.countConcerts();

        // Then
        assertEquals(initialCount + 2, newCount);
    }

    /**
     * Helper method to create and save a concert
     */
    private Concert createAndSaveConcert(String location, LocalDate date) {
        Concert concert = new Concert();
        concert.setLocation(location);
        concert.setDate(date);
        concert.setDescription("Test description");
        return concertService.saveConcert(concert);
    }
}
