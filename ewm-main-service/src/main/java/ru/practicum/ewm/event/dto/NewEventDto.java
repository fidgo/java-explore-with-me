package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.util.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewEventDto {
    @NotNull(groups = {Create.class})
    @Size(min = 20, max = 2000, groups = {Create.class})
    private String annotation;

    private Long category;

    @NotNull(groups = {Create.class})
    @Size(min = 20, max = 7000, groups = {Create.class})
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @NotNull(groups = {Create.class})
    @Min(value = 0, groups = {Create.class})
    private Integer participantLimit;

    @NotNull(groups = {Create.class})
    private Boolean requestModeration;

    @Size(min = 3, max = 120, groups = {Create.class})
    private String title;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Location {
        private float lat;
        private float lon;
    }
}
