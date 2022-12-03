package ru.practicum.ewm.comment.dto;

import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.StatusComment;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.util.DateTimeFormat;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto, User userById, Event eventById) {
        Comment comment = new Comment();

        comment.setId(0L);
        comment.setCreated(LocalDateTime.now());
        comment.setCreator(userById);
        comment.setText(newCommentDto.getText());
        comment.setEvent(eventById);
        comment.setStatus(StatusComment.PENDING);

        return comment;
    }

    public static CommentDto toCommentDto(Comment save) {
        CommentDto commentDto = new CommentDto();

        commentDto.setId(save.getId());
        commentDto.setText(save.getText());
        commentDto.setCommentator(UserMapper.toUserShortDto(save.getCreator()));


        CommentDto.EventSmallDto eventSmallDto = new CommentDto.EventSmallDto();
        eventSmallDto.setId(save.getEvent().getId());
        eventSmallDto.setEventDate(save.getEvent().getEventDate().format(DateTimeFormat.formatter));
        eventSmallDto.setAnnotation(save.getEvent().getAnnotation());
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(save.getEvent().getCreator().getId());
        userShortDto.setName(save.getEvent().getCreator().getName());
        eventSmallDto.setInitiator(userShortDto);

        commentDto.setEvent(eventSmallDto);
        commentDto.setCreated(save.getCreated());
        commentDto.setStatusComment(save.getStatus());

        return commentDto;
    }

    public static CommentPublicDto toCommentPublicDto(Comment comment) {
        CommentPublicDto dto = new CommentPublicDto();

        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());

        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(comment.getCreator().getId());
        userShortDto.setName(comment.getCreator().getName());
        dto.setCommentator(userShortDto);

        CommentPublicDto.EventSmallDto eventDto = new CommentPublicDto.EventSmallDto();
        eventDto.setId(comment.getEvent().getId());
        eventDto.setAnnotation(comment.getEvent().getAnnotation());
        eventDto.setEventDate(comment.getEvent().getEventDate().format(DateTimeFormat.formatter));
        UserShortDto userDto = new UserShortDto();
        userDto.setId(comment.getEvent().getCreator().getId());
        userDto.setName(comment.getEvent().getCreator().getName());
        eventDto.setInitiator(userDto);
        dto.setEvent(eventDto);

        return dto;
    }
}
