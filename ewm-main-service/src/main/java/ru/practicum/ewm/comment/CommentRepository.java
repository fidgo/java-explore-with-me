package ru.practicum.ewm.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEvent_IdAndStatus(long eventId, StatusComment status, PageRequestFrom pageRequest);

    List<Comment> findAllByEvent_IdAndStatus(long eventId, StatusComment status);

    List<Comment> findAllByStatus(StatusComment status);

    List<Comment> findAllByCreator_Id(long userId, PageRequestFrom pageRequest);

    List<Comment> findAllByCreator_Id(long userId);

    List<Comment> findAllByEvent_Id(long eventId, PageRequestFrom pageRequest);

    List<Comment> findAllByEvent_Id(long eventId);
}
