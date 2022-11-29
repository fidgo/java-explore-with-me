package ru.practicum.ewm.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormat {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static DateTimeFormatter get() {
        return formatter;
    }

    public static LocalDateTime getNow() {
        return LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
    }
}
