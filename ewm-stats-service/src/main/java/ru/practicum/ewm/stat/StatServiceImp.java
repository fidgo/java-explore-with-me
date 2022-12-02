package ru.practicum.ewm.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stat.dto.EndPointHit;
import ru.practicum.ewm.stat.dto.StatMapper;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.util.DateTimeFormat;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.net.URLDecoder.decode;

@Service
@RequiredArgsConstructor
@Slf4j
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

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        try {
            String startUTF8 = toUTF8(start);
            String endUTF8 = toUTF8(end);

            log.info("trying to parse start:{} from:{}", start, startUTF8);
            log.info("trying to parse end:{} from:{}", end, endUTF8);

            startTime = LocalDateTime.parse(toUTF8(start), DateTimeFormat.formatter);
            endTime = LocalDateTime.parse(toUTF8(end), DateTimeFormat.formatter);
        } catch (Throwable th) {
            throw new RuntimeException("problem with dencoding now!!!!");
        }

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

    private String toUTF8(String encoded) throws UnsupportedEncodingException {
        return decode(encoded, "UTF-8");
    }
}
