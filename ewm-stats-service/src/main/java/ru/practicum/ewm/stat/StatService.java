package ru.practicum.ewm.stat;

import ru.practicum.ewm.stat.dto.EndPointHit;
import ru.practicum.ewm.stat.dto.ViewStats;

import java.util.List;
import java.util.Set;

public interface StatService {
    EndPointHit hit(EndPointHit endpointHit);

    List<ViewStats> get(String start, String end, Set<String> uris, Boolean unique);

}
