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

import java.util.HashSet;
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
        Set<Event> inputEvents = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));

        Compilation inputCompilation = CompilationMapper.toCompilation(newCompilationDto, inputEvents);

        return CompilationMapper.toCompilationDto(compilationRepository.save(inputCompilation));
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

        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto getByPublic(Long compId) {
        throwIfTitleNotValid(compId, "compId");

        return compilationRepository.findById(compId)
                .map(CompilationMapper::toCompilationDto)
                .orElseThrow(() -> new NoSuchElemException("Compilation doesn't exist with id="
                        + compId));
    }

    @Override
    @Transactional
    public CompilationDto addEventByAdmin(Long compId, Long eventId) {
        throwIfTitleNotValid(compId, "compId");
        throwIfTitleNotValid(eventId, "eventId");

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
        throwIfTitleNotValid(compId, "compId");
        throwIfTitleNotValid(eventId, "eventId");

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
        throwIfTitleNotValid(compId, "compId");

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
        throwIfTitleNotValid(compId, "compId");

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
        throwIfTitleNotValid(compId, "compId");

        compilationRepository.findById(compId).ifPresentOrElse(
                (value) -> {
                    compilationRepository.deleteById(compId);
                },
                () -> {
                    throw new NoSuchElemException("Compilation doesn't exist with id="
                            + compId);
                });
    }

    private void throwIfTitleNotValid(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + " is null");
        }
    }
}
