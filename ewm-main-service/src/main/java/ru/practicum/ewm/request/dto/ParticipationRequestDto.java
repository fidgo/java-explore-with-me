package ru.practicum.ewm.request.dto;

import lombok.*;
import ru.practicum.ewm.request.StateRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParticipationRequestDto {
    private String created;

    private Long event;

    private Long id;

    private Long requester;

    private StateRequest status;
}
