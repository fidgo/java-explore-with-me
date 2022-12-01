package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.error.NoSuchElemException;
import ru.practicum.ewm.error.StateElemException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.http.client.StatClient;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.StateRequest;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.util.DateTimeFormat;
import ru.practicum.ewm.util.PageRequestFrom;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImp implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

    @Override
    @Transactional
    public EventFullDto createByPrivate(NewEventDto newEventDto, Long userId) {
        checkArgumentAndIfNullThrowException(userId, "userId");

        Category inputCategory = null;
        if (newEventDto.getCategory() != null) {
            Long catId = newEventDto.getCategory();
            inputCategory = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NoSuchElemException("Category doesn't exist with id=" + catId));
        }

        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));
        Event inputEvent = EventMapper.toEvent(newEventDto, inputCategory, userById);
        Event save = eventRepository.save(inputEvent);

        return EventMapper.toEventFullDto(save);
    }

    @Override
    @Transactional
    public EventFullDto updateByPrivate(UpdateEventRequestDto updateEventDto, Long userId) {
        checkArgumentAndIfNullThrowException(userId, "userId");

        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event eventById = eventRepository.findById(updateEventDto.getEventId())
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + updateEventDto.getEventId()));

        if (eventById.getCreator().getId().longValue() != userById.getId().longValue()) {
            throw new StateElemException("Update event incorrect. Current event with id=" + eventById.getId()
                    + " created by user=" + eventById.getCreator().getId() + " isn't created by user with id="
                    + userById.getId());
        }

        StateEvent currentState = eventById.getState();
        if ((currentState != StateEvent.PENDING) && (currentState != StateEvent.CANCELED)) {
            throw new StateElemException("Update event incorrect. Wrong State in updateable event:"
                    + eventById.getState());
        }

        Category newCategory = categoryRepository.findById(updateEventDto.getCategory()).orElse(null);
        updateEvent(eventById, updateEventDto, newCategory);

        return EventMapper.toEventFullDto(eventById);
    }

    @Override
    public List<EventFullDto> getListByPrivate(Long userId, PageRequestFrom pageRequest) {
        checkArgumentAndIfNullThrowException(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        List<Event> eventsFromCreator = eventRepository.findAllByCreator_Id(userId, pageRequest);
        return EventMapper.toEventFullDtos(eventsFromCreator);
    }

    @Override
    public EventFullDto getByPrivate(Long eventId, Long userId) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (eventById.getCreator().getId().longValue() != userId.longValue()) {
            throw new StateElemException("Getting event incorrect. Current event with id=" + eventById.getId()
                    + " created by user=" + eventById.getCreator().getId() + " isn't created by user with id="
                    + userId);
        }

        return EventMapper.toEventFullDto(eventById);
    }

    @Override
    @Transactional
    public EventFullDto cancelByPrivate(Long eventId, Long userId) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        checkArgumentAndIfNullThrowException(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (eventById.getCreator().getId().longValue() != userId.longValue()) {
            throw new StateElemException("Cancel event incorrect. Current event with id=" + eventById.getId()
                    + " created by user=" + eventById.getCreator().getId() + " isn't created by user with id="
                    + userId);
        }

        StateEvent current = eventById.getState();
        if (current != StateEvent.PENDING) {
            throw new StateElemException("Cancel event incorrect. Incorrect state:" + current + " Has to be:"
                    + StateEvent.PENDING);
        }

        eventById.setState(StateEvent.CANCELED);
        return EventMapper.toEventFullDto(eventById);
    }

    @Override
    @Transactional
    public EventFullDto editEventByAdmin(long eventId, AdminUpdateEventRequestDto adminUpdateEventRequestDto) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        Category newCategory = categoryRepository.findById(adminUpdateEventRequestDto.getCategory()).orElse(null);
        updateEvent(eventById, adminUpdateEventRequestDto, newCategory);

        return EventMapper.toEventFullDto(eventById);
    }

    @Override
    @Transactional
    public EventFullDto publishByAdmin(Long eventId) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (eventById.getState() != StateEvent.PENDING) {
            throw new StateElemException("Event with id=" + eventId
                    + " isn't in state" + StateEvent.PENDING);
        }

        eventById.setState(StateEvent.PUBLISHED);
        eventById.setPublishedOn(LocalDateTime.now());

        return EventMapper.toEventFullDto(eventById);
    }

    @Override
    @Transactional
    public EventFullDto rejectByAdmin(Long eventId) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (eventById.getState() != StateEvent.PENDING) {
            throw new StateElemException("Event with id=" + eventId
                    + " isn't in state" + StateEvent.PENDING);
        }

        eventById.setState(StateEvent.CANCELED);
        return EventMapper.toEventFullDto(eventById);
    }

    @Override
    public List<EventFullDto> getListByAdmin(List<Long> users, List<StateEvent> states, List<Long> categories,
                                             String rangeStart, String rangeEnd, PageRequestFrom pageRequest) {
        Specification<Event> specification = prepareFilterSpecification(users, states, categories,
                rangeStart, rangeEnd);

        List<Event> events = eventRepository.findAll(specification, pageRequest);

        return EventMapper.toEventFullDtos(events);
    }

    @Override
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        checkArgumentAndIfNullThrowException(eventId, "eventId");
        statClient.saveHit(request);

        Event publishedEventById = eventRepository.findByIdAndState(eventId, StateEvent.PUBLISHED)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId + " and with state:" + StateEvent.PUBLISHED));


        EventFullDto eventFullDto = EventMapper.toEventFullDto(publishedEventById);

        Map<Long, Long> views = statClient.getViewsForEvents(List.of(publishedEventById), false);
        eventFullDto.setViews(views.get(eventId));

        Integer confirmedRequests = requestRepository.countAllByEvent_IdAndStatus(eventId, StateRequest.CONFIRMED);
        eventFullDto.setConfirmedRequests(confirmedRequests);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getListByPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, PageRequestFrom pageRequest,
                                               HttpServletRequest request) {
        statClient.saveHit(request);

        Specification<Event> specification = prepareFilterSpecification(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable);

        List<Event> eventsFromSpec = eventRepository.findAll(specification, pageRequest);
        List<EventShortDto> dtos = EventMapper.toEventShortDtos(eventsFromSpec);

        Map<Long, Long> views = statClient.getViewsForEvents(eventsFromSpec, false);
        dtos.forEach((dto) -> {
                    dto.setViews(views.get(dto.getId()));
                }
        );

        dtos.forEach((dto) -> {
            Integer countConfirmedRequests =
                    requestRepository.countAllByEvent_IdAndStatus(dto.getId(), StateRequest.CONFIRMED);
            dto.setConfirmedRequests(countConfirmedRequests);
        });

        return EventMapper.toEventShortDtos(eventsFromSpec);
    }

    private Specification<Event> prepareFilterSpecification(String text, List<Long> categories, Boolean paid,
                                                            String rangeStart, String rangeEnd, Boolean onlyAvailable) {

        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("state"), StateEvent.PUBLISHED));

            if (paid != null) {
                predicates.add(builder.equal(root.get("paid"), paid));
            }

            if (text != null && !text.isEmpty()) {
                predicates.add(builder.or(builder.like(builder.lower(root.get("annotation")),
                                "%" + text.toLowerCase() + "%"),
                        builder.like(builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
            }

            if ((rangeStart != null && rangeEnd != null)) {
                predicates.add(builder.greaterThan(root.get("eventDate"), LocalDateTime.parse(rangeStart,
                        DateTimeFormat.formatter)));
                predicates.add(builder.lessThan(root.get("eventDate"), LocalDateTime.parse(rangeEnd,
                        DateTimeFormat.formatter)));
            }

            if (categories != null) {
                predicates.add(builder.and(root.get("category").in(categories)));
            }

            if (onlyAvailable) {
                predicates.add(builder.or(builder.equal(root.get("participantLimit"), 0),
                        builder.and(builder.notEqual(root.get("participantLimit"), 0),
                                builder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests")))));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Event> prepareFilterSpecification(List<Long> users, List<StateEvent> states,
                                                            List<Long> categories, String rangeStart,
                                                            String rangeEnd) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty()) {
                predicates.add(builder.and(root.get("creator").in(users)));
            }

            if ((rangeStart != null && rangeEnd != null)) {
                predicates.add(builder.greaterThan(root.get("eventDate"),
                        LocalDateTime.parse(rangeStart, DateTimeFormat.formatter)));
                predicates.add(builder.lessThan(root.get("eventDate"),
                        LocalDateTime.parse(rangeEnd, DateTimeFormat.formatter)));
            }

            if (categories != null && !categories.isEmpty()) {
                predicates.add(builder.and(root.get("category").in(categories)));
            }

            if (states != null && !states.isEmpty()) {
                predicates.add(builder.and(root.get("state").in(states)));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void updateEvent(Event eventFromId, AdminUpdateEventRequestDto adminUpdateEventRequestDto,
                             Category newCategory) {
        if (adminUpdateEventRequestDto.getAnnotation() != null) {
            eventFromId.setAnnotation(adminUpdateEventRequestDto.getAnnotation());
        }

        if (newCategory != null) {
            eventFromId.setCategory(newCategory);
        }

        if (adminUpdateEventRequestDto.getLocation() != null) {
            eventFromId.setLon(adminUpdateEventRequestDto.getLocation().getLon());
            eventFromId.setLat(adminUpdateEventRequestDto.getLocation().getLat());
        }

        if (adminUpdateEventRequestDto.getDescription() != null) {
            eventFromId.setDescription(adminUpdateEventRequestDto.getDescription());
        }

        if (adminUpdateEventRequestDto.getEventDate() != null) {
            eventFromId.setEventDate(LocalDateTime.parse(adminUpdateEventRequestDto.getEventDate(),
                    DateTimeFormat.formatter));
        }

        if (adminUpdateEventRequestDto.getPaid() != null) {
            eventFromId.setPaid(adminUpdateEventRequestDto.getPaid());
        }

        if (adminUpdateEventRequestDto.getTitle() != null) {
            eventFromId.setTitle(adminUpdateEventRequestDto.getTitle());
        }

        if (adminUpdateEventRequestDto.getParticipantLimit() != null) {
            eventFromId.setParticipantLimit(adminUpdateEventRequestDto.getParticipantLimit());
        }
    }

    private void updateEvent(Event currentEvent, UpdateEventRequestDto updateEventDto, Category newCategory) {
        if (updateEventDto.getAnnotation() != null) {
            currentEvent.setAnnotation(updateEventDto.getAnnotation());
        }

        if (newCategory != null) {
            currentEvent.setCategory(newCategory);
        }

        if (updateEventDto.getDescription() != null) {
            currentEvent.setDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.getEventDate() != null) {
            currentEvent.setEventDate(updateEventDto.getEventDate());
        }

        if (updateEventDto.getPaid() != null) {
            currentEvent.setPaid(updateEventDto.getPaid());
        }

        if (updateEventDto.getParticipantLimit() != null) {
            currentEvent.setParticipantLimit(updateEventDto.getParticipantLimit());
        }

        if (updateEventDto.getTitle() != null) {
            currentEvent.setTitle(updateEventDto.getTitle());
        }

        currentEvent.setState(StateEvent.PENDING);
    }

    private void checkArgumentAndIfNullThrowException(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }
}
