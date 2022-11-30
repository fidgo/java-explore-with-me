package ru.practicum.ewm.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.EndPointHit;
import ru.practicum.ewm.stat.dto.ViewStats;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public EndPointHit hit(@RequestBody EndPointHit endpointHit) {

        log.info("{}:{}:EndPointHit:{}",
                "StatController",
                "hit",
                endpointHit
        );

        return statService.hit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> get(@RequestParam String start,
                               @RequestParam String end,
                               @RequestParam Set<String> uris,
                               @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        log.info("{}:{}: start:{}, end:{}, urls:{}, unique:{}",
                "StatController",
                "get",
                start,
                end,
                uris,
                unique
        );

        return statService.get(start, end, uris, unique);
    }
}
