package ru.practicum.ewm.user.dto;

import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.permission.policy.StateSecurity;

public class UserMapper {
    public static User toUser(NewUserRequestDto newUserRequestDto) {
        return new User(
                0L,
                newUserRequestDto.getName(),
                newUserRequestDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;

        /*
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

         */

    }

    public static UserShortDto toUserShortDto(User creator) {
        return new UserShortDto(creator.getId(), creator.getName());
    }

    public static UserDto toUserDto(User user, StateSecurity state) {
        UserDto dto = toUserDto(user);
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setStateSecurity(state);

        return dto;
    }
}
