package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.error.NoSuchElemException;
import ru.practicum.ewm.error.StateElemException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImp implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createByAdmin(NewCompilationDto newCompilationDto) {
        Set<Event> eventsFromDto = newCompilationDto.getEvents().stream()
                .map((id) -> eventRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id=" + id)))
                .collect(Collectors.toSet());

        Compilation inputCompilation = CompilationMapper.toCompilation(newCompilationDto, eventsFromDto);
        Compilation save = compilationRepository.save(inputCompilation);
        return CompilationMapper.toCompilationDto(save);
    }

    @Override
    @Transactional
    public List<CompilationDto> getListByPublic(Boolean pinned, PageRequestFrom pageRequest) {
        List<Compilation> compilations = null;

        if (pinned != null) {
            compilations = compilationRepository.getAllByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll();
        }

        return CompilationMapper.toCompilationDtos(compilations);
    }

    @Override
    @Transactional
    public CompilationDto getByPublic(Long compId) {
        checkArgumentAndIfNullThrowException(compId, "compId");

        Compilation compilationById = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElemException("Compilation doesn't exist with id="
                        + compId));

        return CompilationMapper.toCompilationDto(compilationById);
    }

    @Override
    @Transactional
    public CompilationDto addEventByAdmin(Long compId, Long eventId) {
        checkArgumentAndIfNullThrowException(compId, "compId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");

        Compilation compilationById = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElemException("Compilation doesn't exist with id="
                        + compId));

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        compilationById.getEvents().add(eventById);
        return CompilationMapper.toCompilationDto(compilationById);
    }

    @Override
    @Transactional
    public CompilationDto deleteEventByAdmin(Long compId, Long eventId) {
        checkArgumentAndIfNullThrowException(compId, "compId");
        checkArgumentAndIfNullThrowException(eventId, "eventId");

        Compilation compilationById = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElemException("Compilation doesn't exist with id="
                        + compId));

        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        if (!(compilationById.getEvents().contains(eventById))) {
            throw new StateElemException("Compiltaion(id=" + compilationById.getId()
                    + ")  doesn't contain event(id=" + eventById.getId()
                    + ")");
        }

        compilationById.getEvents().remove(eventById);
        return CompilationMapper.toCompilationDto(compilationById);
    }

    @Override
    @Transactional
    public CompilationDto addPinByAdmin(Long compId) {
        checkArgumentAndIfNullThrowException(compId, "compId");

        Compilation compilationById = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElemException("Compilation doesn't exist with id="
                        + compId));

        if (compilationById.getPinned() != null) {
            compilationById.setPinned(true);
        }

        return CompilationMapper.toCompilationDto(compilationById);
    }

    @Override
    @Transactional
    public void deletePinByAdmin(Long compId) {
        checkArgumentAndIfNullThrowException(compId, "compId");

        Compilation compilationById = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElemException("Compilation doesn't exist with id="
                        + compId));

        if (compilationById.getPinned() != null) {
            compilationById.setPinned(false);
        }
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long compId) {
        checkArgumentAndIfNullThrowException(compId, "compId");

        compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElemException("Compilation doesn't exist with id="
                        + compId));

        compilationRepository.deleteById(compId);
    }

    private void checkArgumentAndIfNullThrowException(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + " is null");
        }
    }
}
