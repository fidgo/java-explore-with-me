package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.util.Create;
import ru.practicum.ewm.util.PageRequestFrom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    public CompilationDto createByAdmin(@Validated({Create.class}) @RequestBody NewCompilationDto newCompilationDto,
                                        HttpServletRequest request) {
        log.info("{}:{}:{}#To create compilation with compilation={}",
                this.getClass().getSimpleName(),
                "createByAdmin",
                request.getRequestURI(),
                newCompilationDto
        );

        return compilationService.createByAdmin(newCompilationDto);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public CompilationDto addEventByAdmin(@PathVariable Long compId,
                                          @PathVariable Long eventId,
                                          HttpServletRequest request) {
        log.info("{}:{}:{}#To add  event:{} to compilation with id:{}",
                this.getClass().getSimpleName(),
                "getByPublic",
                request.getRequestURI(),
                eventId,
                compId
        );

        return compilationService.addEventByAdmin(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/pin")
    public CompilationDto addPinByAdmin(@PathVariable Long compId,
                                        HttpServletRequest request) {
        log.info("{}:{}:{}#To pin  compilation:{}",
                this.getClass().getSimpleName(),
                "addPinByAdmin",
                request.getRequestURI(),
                compId
        );

        return compilationService.addPinByAdmin(compId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getListByPublic(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                @RequestParam(name = "pinned", required = false) Boolean pinned,
                                                HttpServletRequest request) {
        log.info("{}:{}:{}#To get compilations from:{} size:{} pinned: {}",
                this.getClass().getSimpleName(),
                "getListByPublic",
                request.getRequestURI(),
                from,
                size,
                pinned
        );

        final PageRequestFrom pageRequest = new PageRequestFrom(size, from, Sort.unsorted());

        return compilationService.getListByPublic(pinned, pageRequest);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getByPublic(@PathVariable("compId") Long compId,
                                      HttpServletRequest request) {
        log.info("{}:{}:{}#To get compilation with id:{}",
                this.getClass().getSimpleName(),
                "getByPublic",
                request.getRequestURI(),
                compId
        );

        return compilationService.getByPublic(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public CompilationDto deleteEventByAdmin(@PathVariable Long compId,
                                             @PathVariable Long eventId,
                                             HttpServletRequest request) {
        log.info("{}:{}:{}#To delete  event:{} from compilation with id:{}",
                this.getClass().getSimpleName(),
                "deleteEventByAdmin",
                request.getRequestURI(),
                eventId,
                compId
        );

        return compilationService.deleteEventByAdmin(compId, eventId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin")
    public void deletePinByAdmin(@PathVariable Long compId,
                                 HttpServletRequest request) {
        log.info("{}:{}:{}#To remove pin from compilation:{}",
                this.getClass().getSimpleName(),
                "deletePinByAdmin",
                request.getRequestURI(),
                compId
        );

        compilationService.deletePinByAdmin(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public void deleteByAdmin(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}:{}:{}#To delete compilation:{}",
                this.getClass().getSimpleName(),
                "deletePinByAdmin",
                request.getRequestURI(),
                compId
        );

        compilationService.deleteByAdmin(compId);
    }

}

