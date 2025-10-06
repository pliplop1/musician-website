// /src/main/java/com/docker/repository/BiographyRepository.java

package com.docker.repository;

import com.docker.entity.Biography;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiographyRepository extends JpaRepository<Biography, Long> {
}