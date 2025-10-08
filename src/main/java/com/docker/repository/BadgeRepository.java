package com.docker.repository;

import com.docker.entity.Badge;
import com.docker.entity.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByCode(String code);

    List<Badge> findByType(BadgeType type);

    List<Badge> findByTypeOrderByRequiredCountAsc(BadgeType type);
}
