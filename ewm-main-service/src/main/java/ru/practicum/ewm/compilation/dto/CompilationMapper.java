package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.dto.EventMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> eventsFromDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setEvents(eventsFromDto);

        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation save) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setEvents(save.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toSet())
        );

        compilationDto.setPinned(save.getPinned());
        compilationDto.setId(save.getId());
        compilationDto.setTitle(save.getTitle());

        return compilationDto;
    }
}
