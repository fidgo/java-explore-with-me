package ru.practicum.ewm.request;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createByPrivate(Long userId, Long eventId);

    ParticipationRequestDto cancelByPrivate(Long userId, Long requestId);

    List<ParticipationRequestDto> getListByPrivate(Long userId);

    List<ParticipationRequestDto> getListToEventFromCreatorByPrivate(Long userId, Long eventId);

    ParticipationRequestDto confirmByPrivate(Long userId, Long eventId, Long requestId);

    ParticipationRequestDto rejectByPrivate(Long userId, Long eventId, Long requestId);

}
