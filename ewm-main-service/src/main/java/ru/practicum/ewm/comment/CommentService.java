package ru.practicum.ewm.comment;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentPublicDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;

public interface CommentService {
    CommentDto createByPrivate(NewCommentDto newCommentDto, Long userId, Long eventId);

    List<CommentPublicDto> getListByPublic(Long eventId, PageRequestFrom pageRequest);

    List<CommentDto> getListByPrivate(Long userId, PageRequestFrom pageRequest);

    CommentDto getByPrivate(Long userId, Long commentId);

    List<CommentDto> getListByAdmin(Long eventId, String state, PageRequestFrom pageRequest);

    CommentDto updateByAdmin(NewCommentDto newCommentDto, Long commentId);

    CommentDto getByAdmin(Long commentId);

    CommentDto publishByAdmin(Long commentId);

    CommentDto rejectByAdmin(Long commentId);

    CommentDto updateByPrivate(NewCommentDto newCommentDto, Long userId, Long commentId);

    void deleteRejectedByAdmin();

    void deleteFromEventByAdmin(Long eventId, String state);

    void deleteByAdmin(Long commentId);

    void deleteRejectedByPrivate(Long userId, Long commentId);

}
