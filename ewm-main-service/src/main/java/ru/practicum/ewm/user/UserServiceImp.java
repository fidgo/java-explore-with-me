package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.comment.StatusComment;
import ru.practicum.ewm.error.AlreadyExistException;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.error.NoSuchElemException;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.permission.policy.PermissionPolicyMapper;
import ru.practicum.ewm.user.permission.policy.PermissionPolicyRepository;
import ru.practicum.ewm.user.permission.policy.StateSecurity;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final PermissionPolicyRepository permissionPolicyRepository;

    private final CommentRepository commentRepository;

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

        List<UserDto> usersDtos = userRepository.findAllByIdIn(ids, pageRequest)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        Map<Long, StateSecurity> idsSecurity =
                permissionPolicyRepository
                        .getIdsUserAndSecurityState(ids)
                        .stream()
                        .collect(Collectors
                                .toMap(IdUserToSecurityState::getIdUser, IdUserToSecurityState::getSecurityState)
                        );


        for (UserDto dto : usersDtos) {
            if (idsSecurity.containsKey(dto.getId())) {
                dto.setStateSecurity(idsSecurity.get(dto.getId()));
            }
        }

        return usersDtos;
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long userId) {
        throwIfTitleNotValid(userId, "userId");
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserDto setSecurityStatusByAdmin(Long userId, StateSecurity state) {
        throwIfTitleNotValid(userId, "userId");
        throwIfTitleNotValid(state, "userId");

        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        permissionPolicyRepository.findById(userId).ifPresentOrElse(
                (permissionPolicy -> {
                    evaluateCommentStatus(userId, state);
                    permissionPolicy.setStateSecurity(state);
                }),
                () -> {
                    permissionPolicyRepository.save(PermissionPolicyMapper.toPermissionPolicy(userById, state));
                }
        );

        return UserMapper.toUserDto(userById, state);
    }

    private void evaluateCommentStatus(Long userId, StateSecurity state) {
        if (state == StateSecurity.BANNED) {
            List<Comment> commentList = commentRepository.findAllByCreator_Id(userId);
            commentList.forEach(comment -> {
                comment.setStatus(StatusComment.REJECT);
            });
        }

    }

    private void throwIfTitleNotValid(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }
}
