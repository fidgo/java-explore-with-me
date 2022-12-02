package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByEvent_IdAndRequester_Id(long eventId, long requesterId);

    List<Request> findAllByRequester_Id(long requesterId);

    Integer countAllByEvent_IdAndStatus(long eventId, StateRequest status);

    @Query(value = "SELECT r.event_id AS idEvent, COUNT(r.event_id) AS countStatusRequests "
            + "FROM requests AS r WHERE r.event_id IN (?1) AND r.status = ?2 GROUP BY r.event_id", nativeQuery = true)
    List<IdEventToCountRequests> getListEventIdsToCountedRequestsWithStatus(List<Long> ids, String status);

    List<Request> findAllByEvent_Id(long eventId);

    List<Request> findAllByEvent_IdAndRequester_IdAndStatus(long eventId, long requesterId, StateRequest status);

}
