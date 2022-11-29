package ru.practicum.ewm.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminUpdateEventRequestDto {

    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private AdminUpdateEventRequestDto.Location location;

    private Boolean paid;

    private Integer participantLimit;

    private String title;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Location {
        private Float lat;
        private Float lon;
    }
}
