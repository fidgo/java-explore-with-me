package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.AdminUpdateEventRequestDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequestDto;
import ru.practicum.ewm.util.PageRequestFrom;

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

}
