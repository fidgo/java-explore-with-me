package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.util.Create;
import ru.practicum.ewm.util.PageRequestFrom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    UserDto createByAdmin(@Validated({Create.class}) @RequestBody NewUserRequestDto newUserRequestDto,
                          HttpServletRequest request) {
        log.info("{}:{}:{}#To create new user from:{}",
                this.getClass().getSimpleName(),
                "createByAdmin",
                request.getRequestURI(),
                newUserRequestDto);

        return userService.createByAdmin(newUserRequestDto);
    }

    @GetMapping("/admin/users")
    List<UserDto> getByAdmin(@RequestParam(name = "ids") List<Long> ids,
                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                             @RequestParam(name = "size", defaultValue = "10") Integer size,
                             HttpServletRequest request) {
        log.info("{}:{}:{}#To get users with ids={}, from={}, size={}",
                this.getClass().getSimpleName(),
                "getByAdmin",
                request.getRequestURI(),
                ids,
                from,
                size);

        final PageRequestFrom pageRequest = new PageRequestFrom(size, from, Sort.unsorted());
        return userService.getByAdmin(ids, pageRequest);
    }

    @DeleteMapping("/admin/users/{userId}")
    void deleteByAdmin(@PathVariable("userId") Long userId,
                       HttpServletRequest request) {
        log.info("{}:{}:{}#To delete user with id={}",
                this.getClass().getSimpleName(),
                "deleteByAdmin",
                request.getRequestURI(),
                userId
        );

        userService.deleteByAdmin(userId);
    }
}
