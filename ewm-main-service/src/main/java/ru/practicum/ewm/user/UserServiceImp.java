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

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createByAdmin(NewUserRequestDto newUserRequestDto) {
        if (userRepository.existsByNameOrByEmail(newUserRequestDto.getName(), newUserRequestDto.getEmail())) {
            throw new AlreadyExistException("User with name=" + newUserRequestDto.getName()
                    + " or ematl=" + newUserRequestDto.getEmail() + " already exist");
        }

        User fromDto = UserMapper.toUser(newUserRequestDto);
        User save = userRepository.save(fromDto);
        return UserMapper.toUserDto(save);
    }

    @Override
    @Transactional
    public List<UserDto> getByAdmin(List<Long> ids, PageRequestFrom pageRequest) {
        checkArgumentAndIfNullThrowException(ids, "ids");
        List<User> usersFromID = userRepository.findAllByIdIn(ids, pageRequest);
        return UserMapper.toListUserDto(usersFromID);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long userId) {
        checkArgumentAndIfNullThrowException(userId, "userId");
        userRepository.deleteById(userId);
    }

    private void checkArgumentAndIfNullThrowException(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }
}
