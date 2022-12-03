package ru.practicum.ewm.comment.dto;

import lombok.*;
import ru.practicum.ewm.util.Create;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCommentDto {

    @NotEmpty(groups = {Create.class})
    @Size(max = 5000, groups = {Create.class})
    private String text;
}
