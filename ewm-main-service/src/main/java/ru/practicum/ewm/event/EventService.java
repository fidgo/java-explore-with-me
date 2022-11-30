package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.util.PageRequestFrom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createByPrivate(NewEventDto eventNewDto, Long userId);

    EventFullDto updateByPrivate(UpdateEventRequestDto updateEventDto, Long userId);

    List<EventFullDto> getListByPrivate(Long userId, PageRequestFrom pageRequest);

    EventFullDto getByPrivate(Long eventId, Long userId);

    EventFullDto cancelByPrivate(Long eventId, Long userId);

    EventFullDto editEventByAdmin(long eventId, AdminUpdateEventRequestDto adminUpdateEventRequestDto);

    EventFullDto publishByAdmin(Long eventId);

    EventFullDto rejectByAdmin(Long eventId);

    List<EventFullDto> getListByAdmin(List<Long> users, List<State> states, List<Long> categories, String rangeStart,
                                      String rangeEnd, PageRequestFrom pageRequest);

    EventFullDto getEventByPublic(Long eventId, HttpServletRequest request);

    List<EventShortDto> getListByPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, PageRequestFrom pageRequest,
                                        HttpServletRequest request);
}
