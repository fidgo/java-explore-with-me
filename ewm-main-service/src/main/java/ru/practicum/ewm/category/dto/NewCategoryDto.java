package ru.practicum.ewm.category.dto;

import lombok.*;
import ru.practicum.ewm.util.Create;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCategoryDto {
    @NotEmpty(groups = {Create.class})
    private String name;
}
