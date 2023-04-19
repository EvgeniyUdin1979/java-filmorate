package ru.yandex.practicum.filmorate.controllers.errors;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
