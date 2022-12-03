package ru.practicum.ewm.user;

import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.permission.policy.StateSecurity;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;

public interface UserService {
    UserDto createByAdmin(NewUserRequestDto userDto);

    List<UserDto> getByAdmin(List<Long> ids, PageRequestFrom pageRequest);

    void deleteByAdmin(Long userId);

    UserDto setSecurityStatusByAdmin(Long userId, StateSecurity state);

}
