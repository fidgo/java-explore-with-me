package ru.practicum.ewm.http.client;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ewm.event.Event;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatClient {
    private final WebClient webClient;

    @Autowired
    public StatClient(@Value("${statserv.url}") String serverUrl) {
        webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    public void saveHit(HttpServletRequest request) {
        EndPointHit send =
                new EndPointHit("ewm-main-service",
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LocalDateTime.now());
        hit(send);
    }

    public Map<Long, Long> getViewsForEvents(List<Event> events, Boolean unique) {
        Map<Long, Long> eventViews = new HashMap<>();
        Optional<Event> first = events.stream()
                .min(Comparator.comparing(Event::getEventDate));

        if (first.isEmpty()) {
            return eventViews;
        }

        String start = encodeDateTime(first.get().getDateCreate().withNano(0));
        String end = encodeDateTime(LocalDateTime.now().withNano(0));

        List<String> uris = events.stream()
                .map(event -> String.format("/events/{%d}", event.getId()))
                .collect(Collectors.toList());

        List<ViewStats> viewStats = get(start, end, uris, unique);

        viewStats.forEach(viewStat -> {
            Long idFromUri = Long.parseLong(StringUtils.getDigits(viewStat.getUri()));
            eventViews.put(idFromUri, viewStat.getHits());
        });
        return eventViews;
    }

    private String encodeDateTime(LocalDateTime date) {
        String value = date.toString().replace("T", " ");
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private void hit(EndPointHit endPointHit) {
        webClient
                .post()
                .uri("/hit")
                .body(BodyInserters.fromValue(endPointHit))
                .retrieve()
                .bodyToMono(EndPointHit.class)
                .block();
    }

    private List<ViewStats> get(String start, String end,
                                List<String> uris, Boolean unique) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", separateByComma(uris))
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStats.class)
                .collectList()
                .block();
    }

    private String separateByComma(List<String> uris) {
        return String.join(", ", uris)
                .replace("{", "")
                .replace("}", "");
    }
}
