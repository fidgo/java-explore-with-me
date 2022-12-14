package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.StateEvent;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.util.DateTimeFormat;

import java.time.LocalDateTime;

public class EventMapper {
    public static Event toEvent(NewEventDto newEventDto, Category category, User creator) {
        Event event = new Event();

        event.setId(0L);
        event.setCategory(category);
        event.setCreator(creator);
        event.setDescription(newEventDto.getDescription());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setTitle(newEventDto.getTitle());

        event.setState(StateEvent.PENDING);

        event.setEventDate(newEventDto.getEventDate());
        event.setDateCreate(LocalDateTime.now());

        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());

        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setPaid(newEventDto.getPaid());

        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation(event.getAnnotation());

        eventFullDto.setCategory(new EventFullDto.CategoryDto());
        eventFullDto.getCategory().setId(event.getCategory().getId());
        eventFullDto.getCategory().setName(event.getCategory().getName());

        eventFullDto.setCreatedOn(event.getDateCreate().format(DateTimeFormat.formatter));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(DateTimeFormat.formatter));
        eventFullDto.setId(event.getId());

        eventFullDto.setInitiator(new EventFullDto.UserShortDto());
        eventFullDto.getInitiator().setId(event.getCreator().getId());
        eventFullDto.getInitiator().setName(event.getCreator().getName());

        eventFullDto.setLocation(new EventFullDto.Location());
        eventFullDto.getLocation().setLat(event.getLat());
        eventFullDto.getLocation().setLon(event.getLon());

        eventFullDto.setPublishedOn(eventFullDto.getPublishedOn());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());

        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());

        if (event.getCategory() != null) {
            eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        }

        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getCreator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setEventDate(event.getEventDate().format(DateTimeFormat.formatter));

        return eventShortDto;
    }
}
