package ru.practicum.ewm.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stat.dto.EndPointHit;
import ru.practicum.ewm.stat.dto.StatMapper;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.util.DateTimeFormat;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.net.URLDecoder.decode;

@Service
@RequiredArgsConstructor
public class StatServiceImp implements StatService {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public EndPointHit hit(EndPointHit endpointHit) {
        Stat stat = StatMapper.toStat(endpointHit);
        Stat save = statRepository.save(stat);

        return StatMapper.toEndPointHit(save);
    }

    @Override
    @Transactional
    public List<ViewStats> get(String start, String end, Set<String> uris, Boolean unique) {
        List<ViewStats> viewStats;

        LocalDateTime startTime = LocalDateTime.parse(toUTF8(start), DateTimeFormat.formatter);
        LocalDateTime endTime = LocalDateTime.parse(toUTF8(end), DateTimeFormat.formatter);

        if (unique) {
            viewStats = getUnique(uris, startTime, endTime);
        } else {
            viewStats = getNonUnique(uris, startTime, endTime);
        }

        return viewStats;
    }

    private List<ViewStats> getNonUnique(Set<String> uris, LocalDateTime startTime, LocalDateTime endTime) {
        List<ViewStats> viewStats = new ArrayList<>();

        for (String uri : uris) {
            long hits = statRepository.countByUriAndTimestampIsBetween(uri, startTime, endTime);
            ViewStats view = new ViewStats("ewm-main-service", uri, hits);
            viewStats.add(view);
        }

        return viewStats;
    }

    private List<ViewStats> getUnique(Set<String> uris, LocalDateTime startTime, LocalDateTime endTime) {
        List<ViewStats> viewStats = new ArrayList<>();

        for (String uri : uris) {
            long hits = statRepository.countDistinctByUriAndTimestampIsBetween(uri, startTime, endTime);
            ViewStats view = new ViewStats("ewm-main-service", uri, hits);
            viewStats.add(view);
        }

        return viewStats;
    }

    private String toUTF8(String encoded) {
        return decode(encoded, StandardCharsets.UTF_8);
    }
}
