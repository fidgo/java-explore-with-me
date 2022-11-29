package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.error.NoSuchElemException;
import ru.practicum.ewm.error.StateElemException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.State;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.util.DateTimeFormat;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createByPrivate(Long userId, Long eventId) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(userId, "userId");

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event eventFromId = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (eventFromId.getState() != State.PUBLISHED) {
            throw new StateElemException("Can't attend unpublished event!");
        }

        if (eventFromId.getCreator().equals(requester)) {
            throw new StateElemException("Event creator can't be requester!");
        }

        if (requestRepository.existsByEvent_IdAndRequester_Id(eventId, userId)) {
            throw new StateElemException("This is repeated query. Same eventId=" + eventId
                    + " and userId=" + userId + "already in requestBD!");
        }

        //TODO: если у события достигнут лимит запросов на участие - необходимо вернуть ошибку

        Request request = new Request(0L, eventFromId, requester, DateTimeFormat.getNow(),
                StateRequest.PENDING);
        if (!(eventFromId.getRequestModeration())) {
            request.setStatus(StateRequest.ACCEPTED);
        }

        Request save = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(save);
    }


    @Override
    @Transactional
    public ParticipationRequestDto cancelByPrivate(Long userId, Long requestId) {
        checkArgumentAndIfNullThrowException(requestId, "requestId");
        checkArgumentAndIfNullThrowException(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElemException("Request doesn't exist with id=" + requestId));

        if (request.getRequester().getId().longValue() != userId) {
            throw new StateElemException("Request(id=" + requestId + " doesn't belong to user(id="
                    + userId + ")!");
        }

        request.setStatus(StateRequest.CANCELED);

        Request save = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(save);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getListByPrivate(Long userId) {
        checkArgumentAndIfNullThrowException(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        List<Request> requests = requestRepository.findAllByRequester_Id(userId);
        return RequestMapper.toParticipationRequestDtos(requests);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getListToEventFromCreatorByPrivate(Long userId, Long eventId) {
        checkArgumentAndIfNullThrowException(userId, "userId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (event.getCreator().getId().longValue() != userId) {
            throw new StateElemException("Event(id=" + eventId + " doesn't belong to user(id="
                    + userId + ")!");
        }

        List<Request> requests = requestRepository.findAllByEvent_Id(eventId);
        return RequestMapper.toParticipationRequestDtos(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmByPrivate(Long userId, Long eventId, Long requestId) {
        checkArgumentAndIfNullThrowException(userId, "userId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(requestId, "requestId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElemException("Request doesn't exist with id=" + requestId));

        if ((event.getParticipantLimit() == 0) || !(event.getRequestModeration())) {
            throw new StateElemException("Event doesn't need an approve");
        }

        List<Request> approved
                = requestRepository.findAllByEvent_IdAndRequester_IdAndStatus(eventId, requestId,
                StateRequest.CONFIRMED);
        int participantLeft = event.getParticipantLimit() - approved.size();

        if (participantLeft <= 0) {
            throw new StateElemException("Reached limit participant on event");
        }

        request.setStatus(StateRequest.CONFIRMED);
        Request save = requestRepository.save(request);

        if (participantLeft == 1) {
            List<Request> pendingLeft =
                    requestRepository.findAllByEvent_IdAndRequester_IdAndStatus(eventId, requestId,
                            StateRequest.PENDING);
            pendingLeft
                    .forEach((req -> {
                        req.setStatus(StateRequest.CANCELED);
                        requestRepository.save(req);
                    }));
        }

        return RequestMapper.toParticipationRequestDto(save);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectByPrivate(Long userId, Long eventId, Long requestId) {
        checkArgumentAndIfNullThrowException(userId, "userId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(requestId, "requestId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElemException("Request doesn't exist with id=" + requestId));

        request.setStatus(StateRequest.REJECTED);
        Request save = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(save);
    }

    private void checkArgumentAndIfNullThrowException(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }

    }

}
