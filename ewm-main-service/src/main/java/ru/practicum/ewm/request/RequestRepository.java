package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByEvent_IdAndRequester_Id(long eventId, long requesterId);

    List<Request> findAllByRequester_Id(long requesterId);

    Integer countAllByEvent_IdAndStatus(long eventId, StateRequest status);

    List<Request> findAllByEvent_Id(long eventId);

    List<Request> findAllByEvent_IdAndRequester_IdAndStatus(long eventId, long requesterId, StateRequest status);

}
