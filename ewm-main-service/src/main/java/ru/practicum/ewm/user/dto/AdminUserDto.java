package ru.practicum.ewm.user.dto;

import lombok.*;
import ru.practicum.ewm.user.permission.policy.StateSecurity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminUserDto {

    private Long id;

    private String name;

    private String email;

    private StateSecurity permissionPolicy;
}
