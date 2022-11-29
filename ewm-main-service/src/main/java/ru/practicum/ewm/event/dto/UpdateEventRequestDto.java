package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.util.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateEventRequestDto {
    @Size(min = 20, max = 2000, groups = {Update.class})
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, groups = {Update.class})
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(groups = {Update.class})
    private Long eventId;

    private Boolean paid;

    @NotNull(groups = {Update.class})
    @Min(value = 0, groups = {Update.class})
    private Integer participantLimit;

    @Size(min = 3, max = 120, groups = {Update.class})
    private String title;
}
