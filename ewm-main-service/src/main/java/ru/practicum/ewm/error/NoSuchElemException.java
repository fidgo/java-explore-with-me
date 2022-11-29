package ru.practicum.ewm.error;

public class NoSuchElemException extends RuntimeException {
    public NoSuchElemException(String message) {
        super(message);
    }
}
