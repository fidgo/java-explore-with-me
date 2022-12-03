package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentPublicDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.util.Create;
import ru.practicum.ewm.util.PageRequestFrom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/comments/events/{eventId}/")
    CommentDto createByPrivate(@Validated({Create.class}) @RequestBody NewCommentDto newCommentDto,
                               @PathVariable(value = "userId") Long userId,
                               @PathVariable(value = "eventId") Long eventId,
                               HttpServletRequest request) {
        log.info("{}:{}:{}#To create new comment on event from:{} by user:{} with text:{}",
                this.getClass().getSimpleName(),
                "createByPrivate",
                request.getRequestURI(),
                eventId,
                userId,
                newCommentDto);

        return commentService.createByPrivate(newCommentDto, userId, eventId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    CommentDto updateByPrivate(@Validated({Create.class}) @RequestBody NewCommentDto newCommentDto,
                               @PathVariable(value = "userId") Long userId,
                               @PathVariable(value = "commentId") Long commentId,
                               HttpServletRequest request) {
        log.info("{}:{}:{}#To update comment:{} by user:{} with text:{}",
                this.getClass().getSimpleName(),
                "updateByPrivate",
                request.getRequestURI(),
                commentId,
                userId,
                newCommentDto);

        return commentService.updateByPrivate(newCommentDto, userId, commentId);
    }

    @PatchMapping("/admin/comments/{commentId}")
    CommentDto updateByAdmin(@Validated({Create.class}) @RequestBody NewCommentDto newCommentDto,
                             @PathVariable(value = "commentId") Long commentId,
                             HttpServletRequest request) {
        log.info("{}:{}:{}#To update comment:{} text:{}",
                this.getClass().getSimpleName(),
                "updateByAdmin",
                request.getRequestURI(),
                commentId,
                newCommentDto);

        return commentService.updateByAdmin(newCommentDto, commentId);
    }

    @PatchMapping("/admin/comments/{commentId}/publish")
    CommentDto publishByAdmin(@PathVariable(value = "commentId") Long commentId,
                              HttpServletRequest request) {
        log.info("{}:{}:{}#To publish comment:{}",
                this.getClass().getSimpleName(),
                "publishByAdmin",
                request.getRequestURI(),
                commentId
        );

        return commentService.publishByAdmin(commentId);
    }

    @PatchMapping("/admin/comments/{commentId}/reject")
    CommentDto rejectByAdmin(@PathVariable(value = "commentId") Long commentId,
                             HttpServletRequest request) {
        log.info("{}:{}:{}#To reject comment:{}",
                this.getClass().getSimpleName(),
                "rejectByAdmin",
                request.getRequestURI(),
                commentId
        );

        return commentService.rejectByAdmin(commentId);
    }

    @GetMapping("/comments/{eventId}")
    List<CommentPublicDto> getListByPublic(@PathVariable(value = "eventId") Long eventId,
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") Integer size,
                                           @RequestParam(name = "sort", defaultValue = "DESC") String sort,
                                           HttpServletRequest request) {
        log.info("{}:{}:{}#To get public comments from event:{} from:{} size:{}",
                this.getClass().getSimpleName(),
                "getListByPublic",
                request.getRequestURI(),
                eventId,
                from,
                size);

        PageRequestFrom pageRequest = null;
        if (sort.equals("ASC")) {
            pageRequest = new PageRequestFrom(size, from, Sort.by(Sort.Direction.ASC, "created"));
        } else {
            pageRequest = new PageRequestFrom(size, from, Sort.by(Sort.Direction.DESC, "created"));
        }

        return commentService.getListByPublic(eventId, pageRequest);
    }

    @GetMapping("/users/{userId}/comments")
    List<CommentDto> getListByPrivate(@PathVariable(value = "userId") Long userId,
                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size,
                                      @RequestParam(name = "sort", defaultValue = "DESC") String sort,
                                      HttpServletRequest request) {
        log.info("{}:{}:{}#To get own private comments user:{} from:{} size:{}",
                this.getClass().getSimpleName(),
                "getListByPrivate",
                request.getRequestURI(),
                userId,
                from,
                size);

        PageRequestFrom pageRequest = null;
        if (sort.equals("ASC")) {
            pageRequest = new PageRequestFrom(size, from, Sort.by(Sort.Direction.ASC, "created"));
        } else {
            pageRequest = new PageRequestFrom(size, from, Sort.by(Sort.Direction.DESC, "created"));
        }

        return commentService.getListByPrivate(userId, pageRequest);
    }

    @GetMapping("/users/{userId}/comments/{commentId}")
    CommentDto getByPrivate(@PathVariable(value = "userId") Long userId,
                            @PathVariable(value = "commentId") Long commentId,
                            HttpServletRequest request) {
        log.info("{}:{}:{}#To get own private comment by user:{} comment:{}",
                this.getClass().getSimpleName(),
                "getByPrivate",
                request.getRequestURI(),
                userId,
                commentId
        );

        return commentService.getByPrivate(userId, commentId);
    }

    @GetMapping("/admin/comments/events/{eventId}")
    List<CommentDto> getListByAdmin(@PathVariable(value = "eventId") Long eventId,
                                    @RequestParam(name = "state", defaultValue = "ALL") String state,
                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size,
                                    @RequestParam(name = "sort", defaultValue = "DESC") String sort,
                                    HttpServletRequest request) {
        log.info("{}:{}:{}#To get all comments from event:{} state:{} from:{} size:{}",
                this.getClass().getSimpleName(),
                "getListByAdmin",
                request.getRequestURI(),
                eventId,
                state,
                from,
                size);

        PageRequestFrom pageRequest = null;
        if (sort.equals("ASC")) {
            pageRequest = new PageRequestFrom(size, from, Sort.by(Sort.Direction.ASC, "created"));
        } else {
            pageRequest = new PageRequestFrom(size, from, Sort.by(Sort.Direction.DESC, "created"));
        }

        return commentService.getListByAdmin(eventId, state, pageRequest);
    }

    @GetMapping("/admin/comments/{commentId}")
    CommentDto getByAdmin(@PathVariable(value = "commentId") Long commentId,
                          HttpServletRequest request) {
        log.info("{}:{}:{}#To get own comment by admin comment:{}",
                this.getClass().getSimpleName(),
                "getByAdmin",
                request.getRequestURI(),
                commentId
        );

        return commentService.getByAdmin(commentId);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    void deleteRejectedByPrivate(@PathVariable(value = "userId") Long userId,
                                 @PathVariable(value = "commentId") Long commentId,
                                 HttpServletRequest request) {
        log.info("{}:{}:{}#To delete own created:{}  comment:{}",
                this.getClass().getSimpleName(),
                "deleteRejectedByPrivate",
                request.getRequestURI(),
                userId,
                commentId
        );

        commentService.deleteRejectedByPrivate(userId, commentId);
    }


    @DeleteMapping("/admin/comments/rejected")
    void deleteRejectedByAdmin(HttpServletRequest request) {
        log.info("{}:{}:{}#To delete all rejected comments by",
                this.getClass().getSimpleName(),
                "deleteRejectedByAdmin",
                request.getRequestURI()
        );

        commentService.deleteRejectedByAdmin();
    }

    @DeleteMapping("/admin/comments/events/{eventId}")
    void deleteFromEventByAdmin(@PathVariable(value = "eventId") Long eventId,
                                @RequestParam(name = "state", defaultValue = "ALL") String state,
                                HttpServletRequest request) {
        log.info("{}:{}:{}#To delete all comments by admin from event:{} with state:{}",
                this.getClass().getSimpleName(),
                "deleteFromEventByAdmin",
                request.getRequestURI(),
                eventId,
                state
        );

        commentService.deleteFromEventByAdmin(eventId, state);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    void deleteByAdmin(@PathVariable(value = "commentId") Long commentId,
                       HttpServletRequest request) {
        log.info("{}:{}:{}#To delete  comments:{} by admin",
                this.getClass().getSimpleName(),
                "deleteByAdmin",
                request.getRequestURI(),
                commentId
        );

        commentService.deleteByAdmin(commentId);
    }
}