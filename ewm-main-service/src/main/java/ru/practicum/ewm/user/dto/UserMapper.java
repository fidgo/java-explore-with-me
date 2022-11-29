package ru.practicum.ewm.user.dto;

import ru.practicum.ewm.user.User;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<UserDto> toListUserDto(List<User> usersFromID) {
        return usersFromID.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public static UserShortDto toUserShortDto(User creator) {
        return new UserShortDto(creator.getId(), creator.getName());
    }
}
