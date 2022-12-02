package ru.practicum.ewm.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private List<String> errors;

    private String message;

    private String reason;

    private HttpStatus status;

    private String timestamp;
}
