package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.util.DateTimeFormat;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request save) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(save.getId());
        dto.setRequester(save.getRequester().getId());
        dto.setEvent(save.getEvent().getId());
        dto.setCreated(save.getCreated().format(DateTimeFormat.formatter));
        dto.setStatus(save.getStatus());
        return dto;
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtos(List<Request> requests) {
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }
}
