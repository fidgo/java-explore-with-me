package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.AlreadyExistException;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createByAdmin(NewUserRequestDto newUserRequestDto) {
        if (userRepository.existsByNameOrByEmail(newUserRequestDto.getName(), newUserRequestDto.getEmail())) {
            throw new AlreadyExistException("User with name=" + newUserRequestDto.getName()
                    + " or ematl=" + newUserRequestDto.getEmail() + " already exist");
        }

        User inputUser = UserMapper.toUser(newUserRequestDto);

        return UserMapper.toUserDto(userRepository.save(inputUser));
    }

    @Override
    public List<UserDto> getByAdmin(List<Long> ids, PageRequestFrom pageRequest) {
        throwIfTitleNotValid(ids, "ids");

        return userRepository.findAllByIdIn(ids, pageRequest)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long userId) {
        throwIfTitleNotValid(userId, "userId");
        userRepository.deleteById(userId);
    }

    private void throwIfTitleNotValid(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }
}
