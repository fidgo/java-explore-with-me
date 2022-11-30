package ru.practicum.ewm.http.client;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatClient {
    private final BaseClient baseClient;

    private static final String BASE_URI_EVENT_VIEW = "/events/{%d}";

    public void saveHit(HttpServletRequest request) {
        EndPointHit send =
                new EndPointHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        baseClient.hit(send);
    }

    public Map<Long, Long> getViewsForEvents(List<Event> events, Boolean unique) {
        Optional<Event> firstEvent = events.stream()
                .min(Comparator.comparing(Event::getEventDate));

        if (firstEvent.isEmpty()) {
            return new HashMap<>();
        }

        String startEncoded = encodeDate(firstEvent.get().getDateCreate().withNano(0));
        String endEncoded = encodeDate(LocalDateTime.now().withNano(0));
        List<String> uris = getUris(events);
        List<ViewStats> viewStats = baseClient.get(startEncoded, endEncoded, uris, unique);
        Map<Long, Long> eventViews = new HashMap<>();

        viewStats.forEach(viewStat -> eventViews.put(getIdFromUri(viewStat.getUri()), viewStat.getHits()));
        return eventViews;
    }

    private List<String> getUris(List<Event> events) {
        return events.stream()
                .map(event -> String.format(BASE_URI_EVENT_VIEW, event.getId()))
                .collect(Collectors.toList());
    }

    private String encodeDate(LocalDateTime date) {
        String value = date.toString().replace("T", " ");
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private Long getIdFromUri(String uri) {
        return Long.parseLong(StringUtils.getDigits(uri));
    }
}
