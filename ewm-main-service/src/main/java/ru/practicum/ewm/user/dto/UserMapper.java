package ru.practicum.ewm.user.dto;

import ru.practicum.ewm.user.User;

public class UserMapper {
    public static User toUser(NewUserRequestDto newUserRequestDto) {
        return new User(
                0L,
                newUserRequestDto.getName(),
                newUserRequestDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toUserShortDto(User creator) {
        return new UserShortDto(creator.getId(), creator.getName());
    }
}
