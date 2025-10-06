// /src/main/java/com/docker/repository/TrackRepository.java
package com.docker.repository;

import com.docker.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
}