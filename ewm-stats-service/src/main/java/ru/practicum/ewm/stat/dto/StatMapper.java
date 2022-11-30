package ru.practicum.ewm.stat.dto;

import ru.practicum.ewm.stat.Stat;

public class StatMapper {
    public static Stat toStat(EndPointHit endpointHit) {
        return new Stat(
                0L,
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }

    public static EndPointHit toEndPointHit(Stat save) {
        return new EndPointHit(
                save.getApp(),
                save.getUri(),
                save.getIp(),
                save.getTimestamp()
        );
    }
}
