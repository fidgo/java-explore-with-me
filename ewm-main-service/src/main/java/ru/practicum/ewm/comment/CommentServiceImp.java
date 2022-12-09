package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.dto.CommentPublicDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.error.NoSuchElemException;
import ru.practicum.ewm.error.StateElemException;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.permission.policy.PermissionPolicy;
import ru.practicum.ewm.user.permission.policy.PermissionPolicyRepository;
import ru.practicum.ewm.user.permission.policy.StateSecurity;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImp implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PermissionPolicyRepository permissionPolicyRepository;

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto createByPrivate(NewCommentDto newCommentDto, Long userId, Long eventId) {
        throwIfTitleNotValid(eventId, "eventId");
        throwIfTitleNotValid(userId, "userId");

        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));


        Event eventById = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        StateSecurity currenStateSecurity = null;

        Optional<PermissionPolicy> pPolicy = permissionPolicyRepository.findById(userId);
        if (pPolicy.isPresent()) {
            currenStateSecurity = pPolicy.get().getStateSecurity();
        }

        StatusComment newStatusComment = getStatusCommentFromPermissionPolicy(currenStateSecurity);

        Comment inputComment = CommentMapper.toComment(newCommentDto, userById, eventById);

        inputComment.setStatus(newStatusComment);
        return CommentMapper.toCommentDto(commentRepository.save(inputComment));
    }

    @Override
    public List<CommentPublicDto> getListByPublic(Long eventId, PageRequestFrom pageRequest) {
        throwIfTitleNotValid(eventId, "eventId");

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        return commentRepository
                .findAllByEvent_IdAndStatus(eventId, StatusComment.PUBLISHED, pageRequest)
                .stream()
                .map(CommentMapper::toCommentPublicDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getListByPrivate(Long userId, PageRequestFrom pageRequest) {
        throwIfTitleNotValid(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        return commentRepository
                .findAllByCreator_Id(userId, pageRequest)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getByPrivate(Long userId, Long commentId) {
        throwIfTitleNotValid(userId, "userId");
        throwIfTitleNotValid(commentId, "commentId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id=" + commentId))

        );
    }

    @Override
    public List<CommentDto> getListByAdmin(Long eventId, String state, PageRequestFrom pageRequest) {
        throwIfTitleNotValid(eventId, "eventId");

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElemException("Event doesn't exist with id="
                        + eventId));

        List<Comment> commentList = new ArrayList<>();
        if ((state == null) || ("ALL".equals(state))) {
            commentList = commentRepository.findAllByEvent_Id(eventId, pageRequest);
        } else {
            StatusComment statusComment = StatusComment.valueOf(state);
            commentList = commentRepository.findAllByEvent_IdAndStatus(eventId, statusComment, pageRequest);
        }


        return commentList
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateByAdmin(NewCommentDto newCommentDto, Long commentId) {
        throwIfTitleNotValid(commentId, "commentId");

        Comment inputComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id="
                        + commentId));
        inputComment.setText(newCommentDto.getText());

        return CommentMapper.toCommentDto(inputComment);
    }

    @Override
    public CommentDto getByAdmin(Long commentId) {
        throwIfTitleNotValid(commentId, "commentId");

        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id=" + commentId))

        );
    }

    @Override
    @Transactional
    public CommentDto publishByAdmin(Long commentId) {
        throwIfTitleNotValid(commentId, "commentId");

        Comment inputComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id="
                        + commentId));

        PermissionPolicy pPolicy = permissionPolicyRepository.findById(inputComment
                        .getCreator()
                        .getId())
                .orElseThrow(() -> new NoSuchElemException("User creator doesn't exist"));

        StateSecurity stateSecurity = pPolicy.getStateSecurity();

        if ((stateSecurity == StateSecurity.LIMITED) && (inputComment.getStatus() == StatusComment.PENDING)) {
            inputComment.setStatus(StatusComment.PUBLISHED);
        } else {
            throw new StateElemException("Can't change to publish, stateSecurity:" + stateSecurity +
                    " or statusComment:" + inputComment.getStatus());
        }

        return CommentMapper.toCommentDto(inputComment);
    }

    @Override
    @Transactional
    public CommentDto rejectByAdmin(Long commentId) {
        throwIfTitleNotValid(commentId, "commentId");

        Comment inputComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id="
                        + commentId));

        Optional<PermissionPolicy> optPolicy = permissionPolicyRepository.findById(inputComment
                .getCreator()
                .getId());

        if ((inputComment.getStatus() == StatusComment.PENDING) &&
                (optPolicy.isPresent()) &&
                (optPolicy.get().getStateSecurity() == StateSecurity.LIMITED)) {
            inputComment.setStatus(StatusComment.REJECT);
        } else {
            throw new StateElemException("Can't change to publish");
        }

        return CommentMapper.toCommentDto(inputComment);
    }

    @Override
    @Transactional
    public CommentDto updateByPrivate(NewCommentDto newCommentDto, Long userId, Long commentId) {
        throwIfTitleNotValid(userId, "userId");
        throwIfTitleNotValid(commentId, "commentId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Comment inputComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id="
                        + commentId));

        if (inputComment.getCreator().getId().longValue() != userId) {
            throw new StateElemException("User=" + userId + " isn't a creator of comment=" + commentId);
        }

        Optional<PermissionPolicy> optPolicy = permissionPolicyRepository.findById(inputComment
                .getCreator()
                .getId());

        if ((inputComment.getStatus() == StatusComment.PUBLISHED) &&
                (optPolicy.isPresent()) &&
                (optPolicy.get().getStateSecurity() == StateSecurity.LIMITED)
        ) {
            inputComment.setStatus(StatusComment.PENDING);
        }

        inputComment.setText(newCommentDto.getText());

        return CommentMapper.toCommentDto(inputComment);
    }

    @Override
    @Transactional
    public void deleteRejectedByAdmin() {
        commentRepository.deleteAll(commentRepository.findAllByStatus(StatusComment.REJECT));
    }

    @Override
    @Transactional
    public void deleteFromEventByAdmin(Long eventId, String state) {
        throwIfTitleNotValid(eventId, "eventId");

        List<Comment> commentList = new ArrayList<>();
        if ((state == null) || ("ALL".equals(state))) {
            commentList = commentRepository.findAllByEvent_Id(eventId);
        } else {
            StatusComment statusComment = StatusComment.valueOf(state);
            commentList = commentRepository.findAllByEvent_IdAndStatus(eventId, statusComment);
        }

        commentRepository.deleteAll(commentList);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {
        throwIfTitleNotValid(commentId, "commentId");

        Comment inputComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id="
                        + commentId));

        commentRepository.delete(inputComment);
    }

    @Override
    @Transactional
    public void deleteRejectedByPrivate(Long userId, Long commentId) {
        throwIfTitleNotValid(userId, "userId");
        throwIfTitleNotValid(commentId, "commentId");

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("User doesn't exist with id=" + userId));

        Comment inputComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElemException("Comment doesn't exist with id="
                        + commentId));

        if (inputComment.getCreator().getId().longValue() != userId) {
            throw new StateElemException("User=" + userId + " isn't a creator of comment=" + commentId);
        }

        commentRepository.delete(inputComment);
    }

    private StatusComment getStatusCommentFromPermissionPolicy(StateSecurity currenStateSecurity) {
        StatusComment statusComment = null;

        if (currenStateSecurity == null) {
            return StatusComment.PUBLISHED;
        }

        switch (currenStateSecurity) {
            case FREE:
                statusComment = StatusComment.PUBLISHED;
                break;
            case LIMITED:
                statusComment = StatusComment.PENDING;
                break;
            case BANNED:
                statusComment = StatusComment.REJECT;
                break;
        }
        return statusComment;
    }

    private void throwIfTitleNotValid(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }
}
