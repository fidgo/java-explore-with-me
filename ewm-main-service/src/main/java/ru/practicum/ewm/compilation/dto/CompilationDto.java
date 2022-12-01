package ru.practicum.ewm.compilation.dto;


import lombok.*;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDto {
    private Long id;

    private Boolean pinned;

    private String title;

    private Set<EventShortDto> events;


}
