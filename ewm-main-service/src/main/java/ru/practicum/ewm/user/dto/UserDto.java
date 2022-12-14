package ru.practicum.ewm.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.ewm.user.permission.policy.StateSecurity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;

    private String name;

    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StateSecurity stateSecurity;
}
