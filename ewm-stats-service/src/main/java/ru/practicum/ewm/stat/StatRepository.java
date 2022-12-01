package ru.practicum.ewm.stat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    long countByUriAndTimestampIsBetween(String uri, LocalDateTime start, LocalDateTime end);

    long countDistinctByUriAndTimestampIsBetween(String uri, LocalDateTime start, LocalDateTime end);
}
