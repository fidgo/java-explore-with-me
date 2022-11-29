package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;

public interface CompilationService {
    CompilationDto createByAdmin(NewCompilationDto newCompilationDto);

    List<CompilationDto> getListByPublic(Boolean pinned, PageRequestFrom pageRequest);

    CompilationDto getByPublic(Long compId);

    CompilationDto addEventByAdmin(Long compId, Long eventId);

    CompilationDto deleteEventByAdmin(Long compId, Long eventId);

    CompilationDto addPinByAdmin(Long compId);

    void deletePinByAdmin(Long compId);

    void deleteByAdmin(Long compId);

}
