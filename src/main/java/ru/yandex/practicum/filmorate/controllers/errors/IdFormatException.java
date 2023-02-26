package ru.yandex.practicum.filmorate.controllers.errors;

import org.springframework.http.HttpStatus;

public class IdFormatException extends FilmRequestException{
    public IdFormatException(HttpStatus codeStatus) {
        super(codeStatus);
    }

    public IdFormatException(String message, HttpStatus codeStatus) {
        super(message, codeStatus);
    }

    public IdFormatException(String message, Throwable cause, HttpStatus codeStatus) {
        super(message, cause, codeStatus);
    }

    public IdFormatException(Throwable cause, HttpStatus codeStatus) {
        super(cause, codeStatus);
    }

    public IdFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus codeStatus) {
        super(message, cause, enableSuppression, writableStackTrace, codeStatus);
    }
}
