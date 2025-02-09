package ru.yandex.practicum.filmorate.exception;

public class NonexistentException extends RuntimeException {
    public NonexistentException(String message) {
        super(message);
    }
}
