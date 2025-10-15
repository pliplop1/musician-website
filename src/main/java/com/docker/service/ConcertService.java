package com.docker.service;

import com.docker.entity.Concert;
import com.docker.repository.ConcertRepository;
import com.docker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;

    public ConcertService(ConcertRepository concertRepository, UserRepository userRepository) {
        this.concertRepository = concertRepository;
        this.userRepository = userRepository;
    }

    public Concert saveConcert(Concert concert) {
        return concertRepository.save(concert);
    }

    public List<Concert> findAllConcerts() {
        return concertRepository.findAll();
    }

    public Optional<Concert> findConcertById(Long id) {
        return concertRepository.findById(id);
    }

    @Transactional
    public void deleteConcert(Long id) {
        // Ensure join-table rows are removed before deleting the concert
        userRepository.deleteFavoriteConcertsByConcertId(id);
        concertRepository.deleteById(id);
    }
    public long countConcerts() {
        return concertRepository.count();
    }
}

