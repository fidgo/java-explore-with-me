package ru.practicum.ewm.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController()
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    ParticipationRequestDto createByPrivate(@PathVariable(value = "userId") Long userId,
                                            @RequestParam(name = "eventId", required = false) Long eventId,
                                            HttpServletRequest request) {

        log.info("{}:{}:{}#To create user:{} request on event:{}",
                this.getClass().getSimpleName(),
                "createByPrivate",
                request.getRequestURI(),
                userId,
                eventId);

        return requestService.createByPrivate(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelByPrivate(@PathVariable Long userId,
                                                   @PathVariable Long requestId,
                                                   HttpServletRequest request) {

        log.info("{}:{}:{}#To cancel user:{} request:{}",
                this.getClass().getSimpleName(),
                "cancelByPrivate",
                request.getRequestURI(),
                userId,
                requestId);

        return requestService.cancelByPrivate(userId, requestId);
    }

    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> getListByPrivate(@PathVariable Long userId,
                                                   HttpServletRequest request) {

        log.info("{}:{}:{}#To get all user:{} request",
                this.getClass().getSimpleName(),
                "getListByPrivate",
                request.getRequestURI(),
                userId
        );

        return requestService.getListByPrivate(userId);
    }


    @GetMapping("/users/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getListToEventFromCreatorByPrivate(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     HttpServletRequest request) {

        log.info("{}:{}:{}#To get all request to event:{} by creator:{} request",
                this.getClass().getSimpleName(),
                "getListToEventFromCreatorByPrivate",
                request.getRequestURI(),
                eventId,
                userId
        );

        return requestService.getListToEventFromCreatorByPrivate(userId, eventId);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectByPrivate(@PathVariable(value = "userId") long userId,
                                                   @PathVariable(value = "eventId") long eventId,
                                                   @PathVariable(value = "reqId") long requestId,
                                                   HttpServletRequest request) {

        log.info("{}:{}:{}#To reject request:{} to event:{} by creator:{} request",
                this.getClass().getSimpleName(),
                "rejectByPrivate",
                request.getRequestURI(),
                requestId,
                eventId,
                userId
        );

        return requestService.rejectByPrivate(userId, eventId, requestId);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmByPrivate(@PathVariable(value = "userId") long userId,
                                                    @PathVariable(value = "eventId") long eventId,
                                                    @PathVariable(value = "reqId") long requestId,
                                                    HttpServletRequest request) {

        log.info("{}:{}:{}#To confirm request:{} to event:{} by creator:{} request",
                this.getClass().getSimpleName(),
                "confirmByPrivate",
                request.getRequestURI(),
                requestId,
                eventId,
                userId
        );

        return requestService.confirmByPrivate(userId, eventId, requestId);
    }

}
