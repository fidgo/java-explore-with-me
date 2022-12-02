package ru.practicum.ewm.category.dto;

import lombok.*;
import ru.practicum.ewm.util.Update;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {
    @NotNull(groups = {Update.class})
    private Long id;
    @NotEmpty(groups = {Update.class})
    private String name;
}
