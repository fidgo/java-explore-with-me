package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategory_Id(long id);

    boolean existsByIdIn(List<Long> ids);

    List<Event> findAllByCreator_Id(long creatorId, Pageable pageable);
}
