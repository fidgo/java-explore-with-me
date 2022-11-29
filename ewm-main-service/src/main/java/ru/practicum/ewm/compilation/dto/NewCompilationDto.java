package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.util.Create;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCompilationDto {
    @NotEmpty(groups = {Create.class})
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
