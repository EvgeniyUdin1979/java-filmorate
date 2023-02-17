package ru.yandex.practicum.filmorate.controllers.errors;

import org.springframework.http.HttpStatus;

public class FilmRequestException extends RuntimeException {
    private HttpStatus codeStatus;

    public HttpStatus getCodeStatus() {
        return codeStatus;
    }

    public FilmRequestException(HttpStatus codeStatus) {
        this.codeStatus = codeStatus;
    }

    public FilmRequestException(String message, HttpStatus codeStatus) {
        super(message);
        this.codeStatus = codeStatus;
    }

    public FilmRequestException(String message, Throwable cause, HttpStatus codeStatus) {
        super(message, cause);
        this.codeStatus = codeStatus;
    }

    public FilmRequestException(Throwable cause, HttpStatus codeStatus) {
        super(cause);
        this.codeStatus = codeStatus;
    }

    public FilmRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus codeStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.codeStatus = codeStatus;
    }

    public FilmRequestException() {
    }

    public FilmRequestException(String message) {
        super(message);
    }


    public FilmRequestException(String message, Throwable cause) {
        super(message, cause);

    }

    public FilmRequestException(Throwable cause) {
        super(cause);
    }

    public FilmRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
