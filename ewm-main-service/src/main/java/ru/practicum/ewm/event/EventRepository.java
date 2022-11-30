package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategory_Id(long id);

    Optional<Event> findByIdAndState(long eventId, State state);

    List<Event> findAllByCreator_Id(long creatorId, Pageable pageable);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

}
