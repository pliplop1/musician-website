package com.docker.repository;

import com.docker.entity.Badge;
import com.docker.entity.User;
import com.docker.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUserOrderByUnlockedAtDesc(User user);

    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);

    boolean existsByUserAndBadge(User user, Badge badge);

    @Query("SELECT ub FROM UserBadge ub WHERE ub.user = :user AND ub.notified = false")
    List<UserBadge> findUnnotifiedBadges(@Param("user") User user);

    long countByUser(User user);
}
