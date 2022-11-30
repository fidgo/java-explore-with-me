package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.State;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.util.DateTimeFormat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventMapper {
    public static Event toEvent(NewEventDto newEventDto, Category category, User creator) {
        Event event = new Event();

        event.setId(0L);
        event.setCategory(category);
        event.setCreator(creator);
        event.setDescription(newEventDto.getDescription());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setTitle(newEventDto.getTitle());
        event.setState(State.PENDING);
        event.setEventDate(newEventDto.getEventDate());
        event.setDateCreate(DateTimeFormat.getNow());
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

        //TODO: дописать confirmedRequests - Количество одобренных заявок на участие в данном событии
        eventFullDto.setConfirmedRequests(0L);

        eventFullDto.setCreatedOn(event.getDateCreate().format(DateTimeFormat.get()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(DateTimeFormat.get()));
        eventFullDto.setId(event.getId());

        eventFullDto.setInitiator(new EventFullDto.UserShortDto());
        eventFullDto.getInitiator().setId(event.getCreator().getId());
        eventFullDto.getInitiator().setName(event.getCreator().getName());

        eventFullDto.setLocation(new EventFullDto.Location());
        eventFullDto.getLocation().setLat(event.getLat());
        eventFullDto.getLocation().setLon(event.getLon());

        //TODO: дописать views

        eventFullDto.setPublishedOn(eventFullDto.getPublishedOn());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        return eventFullDto;
    }

    public static List<EventFullDto> toEventFullDtos(List<Event> events) {
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());

        if (event.getCategory() != null) {
            eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        }


        //TODO: confirmerd request у каждого event должен быть подсчитан
        //eventShortDto.setConfirmedRequests(event.);
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getCreator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setEventDate(event.getEventDate().format(DateTimeFormat.get()));

        //TODO: добавить учет views
        //eventShortDto.setViews(event.);
        return eventShortDto;
    }

    public static Set<EventShortDto> toNewEventDtoSet(Set<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toSet());
    }

    public static List<EventShortDto> toEventShortDtos(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }
}
