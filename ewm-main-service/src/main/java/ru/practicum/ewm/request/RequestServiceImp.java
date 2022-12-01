package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.error.NoSuchElemException;
import ru.practicum.ewm.error.StateElemException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.StateEvent;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createByPrivate(Long userId, Long eventId) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(userId, "userId");

        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (eventById.getState() != StateEvent.PUBLISHED) {
            throw new StateElemException("Can't attend unpublished event!");
        }

        if (eventById.getCreator().equals(userById)) {
            throw new StateElemException("Event creator can't be requester!");
        }

        if (requestRepository.existsByEvent_IdAndRequester_Id(eventId, userId)) {
            throw new StateElemException("This is repeated query. Same eventId=" + eventId
                    + " and userId=" + userId + "already in requestBD!");
        }

        Request newRequest = new Request(0L, eventById, userById, LocalDateTime.now(),
                StateRequest.PENDING);
        if (!(eventById.getRequestModeration())) {
            newRequest.setStatus(StateRequest.CONFIRMED);
        }

        Integer participantLimit = eventById.getParticipantLimit();
        Integer confirmed =
                requestRepository.countAllByEvent_IdAndStatus(eventId, StateRequest.CONFIRMED);
        int remainingSpace = participantLimit - confirmed;

        if (remainingSpace <= 0) {
            newRequest.setStatus(StateRequest.REJECTED);
        }

        Request save = requestRepository.save(newRequest);
        return RequestMapper.toParticipationRequestDto(save);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelByPrivate(Long userId, Long requestId) {
        checkArgumentAndIfNullThrowException(requestId, "requestId");
        checkArgumentAndIfNullThrowException(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Request requestById = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElemException("Request doesn't exist with id=" + requestId));

        if (requestById.getRequester().getId().longValue() != userId) {
            throw new StateElemException("Request(id=" + requestId + " doesn't belong to user(id="
                    + userId + ")!");
        }

        requestById.setStatus(StateRequest.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestById);
    }

    @Override
    public List<ParticipationRequestDto> getListByPrivate(Long userId) {
        checkArgumentAndIfNullThrowException(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        List<Request> requests = requestRepository.findAllByRequester_Id(userId);
        return RequestMapper.toParticipationRequestDtos(requests);
    }

    @Override
    public List<ParticipationRequestDto> getListToEventFromCreatorByPrivate(Long userId, Long eventId) {
        checkArgumentAndIfNullThrowException(userId, "userId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (eventById.getCreator().getId().longValue() != userId) {
            throw new StateElemException("Event(id=" + eventId + " doesn't belong to user(id="
                    + userId + ")!");
        }

        List<Request> requestsFromEvent = requestRepository.findAllByEvent_Id(eventId);
        return RequestMapper.toParticipationRequestDtos(requestsFromEvent);
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmByPrivate(Long userId, Long eventId, Long requestId) {
        checkArgumentAndIfNullThrowException(userId, "userId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(requestId, "requestId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        Request requestById = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElemException("Request doesn't exist with id=" + requestId));

        if ((eventById.getParticipantLimit() == 0) || !(eventById.getRequestModeration())) {
            throw new StateElemException("Event doesn't need an approve");
        }

        List<Request> confirmedRequests
                = requestRepository.findAllByEvent_IdAndRequester_IdAndStatus(eventId, requestId,
                StateRequest.CONFIRMED);
        int participantLeft = eventById.getParticipantLimit() - confirmedRequests.size();
        if (participantLeft <= 0) {
            throw new StateElemException("Reached limit participant on event");
        }

        requestById.setStatus(StateRequest.CONFIRMED);

        if (participantLeft == 1) {
            List<Request> pendingRequests =
                    requestRepository.findAllByEvent_IdAndRequester_IdAndStatus(eventId, requestId,
                            StateRequest.PENDING);
            pendingRequests.forEach((req -> {
                req.setStatus(StateRequest.CANCELED);
            }
            ));
        }

        return RequestMapper.toParticipationRequestDto(requestById);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectByPrivate(Long userId, Long eventId, Long requestId) {
        checkArgumentAndIfNullThrowException(userId, "userId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(requestId, "requestId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        Request requestById = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElemException("Request doesn't exist with id=" + requestId));

        requestById.setStatus(StateRequest.REJECTED);
        return RequestMapper.toParticipationRequestDto(requestById);
    }

    private void checkArgumentAndIfNullThrowException(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }

}
