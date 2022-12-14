package ru.practicum.ewm.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.util.Create;
import ru.practicum.ewm.util.PageRequestFrom;
import ru.practicum.ewm.util.Update;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    EventFullDto createByPrivate(@Validated({Create.class}) @RequestBody NewEventDto eventNewDto,
                                 @PathVariable(value = "userId") Long userId,
                                 HttpServletRequest request) {
        log.info("{}:{}:{}#To create new event from:{} by user:{}",
                this.getClass().getSimpleName(),
                "createByAdmin",
                request.getRequestURI(),
                eventNewDto,
                userId);

        return eventService.createByPrivate(eventNewDto, userId);
    }

    @PatchMapping("/users/{userId}/events")
    EventFullDto updateByPrivate(@Validated({Update.class}) @RequestBody UpdateEventRequestDto updateEventDto,
                                 @PathVariable Long userId,
                                 HttpServletRequest request) {
        log.info("{}:{}:{}#To update event from:{} by user:{}",
                this.getClass().getSimpleName(),
                "updateByPrivate",
                request.getRequestURI(),
                updateEventDto,
                userId);

        return eventService.updateByPrivate(updateEventDto, userId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    EventFullDto cancelByPrivate(@PathVariable Long eventId,
                                 @PathVariable Long userId,
                                 HttpServletRequest request) {
        log.info("{}:{}:{}#To cancel event by user:{} and event:{}",
                this.getClass().getSimpleName(),
                "cancelByPrivate",
                request.getRequestURI(),
                userId,
                eventId
        );

        return eventService.cancelByPrivate(eventId, userId);
    }

    @PatchMapping("/admin/events/{eventId}/publish")
    EventFullDto publishByAdmin(@PathVariable("eventId") Long eventId, HttpServletRequest request) {
        log.info("{}:{}:{}#To publish event:{}",
                this.getClass().getSimpleName(),
                "publishByAdmin",
                request.getRequestURI(),
                eventId
        );

        return eventService.publishByAdmin(eventId);
    }

    @PatchMapping("/admin/events/{eventId}/reject")
    EventFullDto rejectByAdmin(@PathVariable("eventId") Long eventId, HttpServletRequest request) {
        log.info("{}:{}:{}#To reject event:{}",
                this.getClass().getSimpleName(),
                "rejectByAdmin",
                request.getRequestURI(),
                eventId
        );

        return eventService.rejectByAdmin(eventId);
    }

    @PutMapping(path = "/admin/events/{eventId}")
    public EventFullDto editEventByAdmin(@PathVariable(value = "eventId") long eventId,
                                         @Validated({Update.class}) @RequestBody AdminUpdateEventRequestDto adminUpdateEventRequestDto,
                                         HttpServletRequest request) {
        log.info("{}:{}:{}#To edit event:{} with {}",
                this.getClass().getSimpleName(),
                "editEventByAdmin",
                request.getRequestURI(),
                eventId,
                adminUpdateEventRequestDto
        );

        return eventService.editEventByAdmin(eventId, adminUpdateEventRequestDto);
    }


    @GetMapping("/users/{userId}/events")
    List<EventFullDto> getListByPrivate(@PathVariable(value = "userId") Long userId,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        log.info("{}:{}:{}#To gets events by user:{} from:{} size:{}",
                this.getClass().getSimpleName(),
                "getListByPrivate",
                request.getRequestURI(),
                userId,
                from,
                size);

        final PageRequestFrom pageRequest = new PageRequestFrom(size, from, Sort.unsorted());

        return eventService.getListByPrivate(userId, pageRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getByPrivate(@PathVariable Long eventId,
                              @PathVariable Long userId,
                              HttpServletRequest request) {
        log.info("{}:{}:{}#To gets event by user:{} and event:{}",
                this.getClass().getSimpleName(),
                "getByPrivate",
                request.getRequestURI(),
                userId,
                eventId
        );

        return eventService.getByPrivate(eventId, userId);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getListByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<StateEvent> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        log.info("{}:{}:{}#To get event by users:{}, states:{}, categories:{}, rangeStart:{}, rangeEnd:{}, from:{}" +
                        " and size:{}",
                this.getClass().getSimpleName(),
                "getListByAdmin",
                request.getRequestURI(),
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );

        final PageRequestFrom pageRequest = new PageRequestFrom(size, from, Sort.unsorted());

        return eventService.getListByAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest);
    }

    @GetMapping("/events/{id}")
    EventFullDto getEventByPublic(@PathVariable("id") Long eventId, HttpServletRequest request) {
        log.info("{}:{}:{}#To get event:{}",
                this.getClass().getSimpleName(),
                "getEventByPublic",
                request.getRequestURI(),
                eventId
        );

        return eventService.getEventByPublic(eventId, request);
    }

    @GetMapping("/events")
    public List<EventShortDto> getListByPublic(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(required = false, defaultValue = "") String sort,
                                               @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        log.info("{}:{}:{}#To get events by text:{}, by categories:{}, by paid:{}, by rangeStart:{}, by rangeEnd:{}, " +
                        " by onlyAvailable:{}, by sort:{}, from:{}, size:{}",
                this.getClass().getSimpleName(), "getListByPublic", request.getRequestURI(), text, categories,
                paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
        );

        Sort sorting = null;
        switch (sort) {
            case "EVENT_DATE":
                sorting = Sort.by(Sort.Direction.DESC, "eventDate");
                break;
            case "VIEWS":
                sorting = Sort.by(Sort.Direction.DESC, "views");
                break;
            default:
                sorting = Sort.by(Sort.Direction.ASC, "id");
        }
        final PageRequestFrom pageRequest = new PageRequestFrom(size, from, sorting);

        return eventService.getListByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageRequest, request);
    }

}
