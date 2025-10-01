package com.docker.service;

import com.docker.entity.Concert;
import com.docker.repository.ConcertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
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

    public void deleteConcert(Long id) {
        concertRepository.deleteById(id);
    }
}

