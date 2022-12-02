package ru.practicum.ewm.user.dto;

import lombok.*;
import ru.practicum.ewm.util.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewUserRequestDto {
    @NotEmpty(groups = {Create.class})
    private String name;

    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class})
    private String email;
}
