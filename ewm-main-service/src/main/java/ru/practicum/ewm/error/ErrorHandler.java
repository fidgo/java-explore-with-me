package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.util.DateTimeFormat;
import ru.practicum.ewm.util.StackTraceToString;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleAlreadyExistException(final NoSuchElemException e) {
        log.info("404 {} {}", e.getMessage(), e);
        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAlreadyExistException(final AlreadyExistException e) {
        log.info("409 {} {}", e.getMessage(), e);
        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "For the requested operation the conditions are not met",
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleStatusElemException(final StateElemException e) {
        log.info("403 {} {}", e.getMessage(), e);
        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "For the requested operation the conditions are not met",
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IlLegalArgumentException e) {
        log.info("400 {} {}", e.getMessage(), e);
        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info("400 {} {}", e.getMessage(), e);
        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        log.info("500 {} {}", e.getMessage(), e);
        return new ApiError(
                StackTraceToString.exec(e),
                e.getMessage(),
                "Error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now().format(DateTimeFormat.formatter)
        );
    }
}
