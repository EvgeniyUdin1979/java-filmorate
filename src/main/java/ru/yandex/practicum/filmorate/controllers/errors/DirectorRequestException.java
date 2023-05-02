package ru.yandex.practicum.filmorate.controllers.errors;

import org.springframework.http.HttpStatus;

public class DirectorRequestException extends RuntimeException {
    private HttpStatus codeStatus;

    public HttpStatus getCodeStatus() {
        return codeStatus;
    }

    public DirectorRequestException(HttpStatus codeStatus) {
        this.codeStatus = codeStatus;
    }

    public DirectorRequestException(String message, HttpStatus codeStatus) {
        super(message);
        this.codeStatus = codeStatus;
    }

    public DirectorRequestException(String message, Throwable cause, HttpStatus codeStatus) {
        super(message, cause);
        this.codeStatus = codeStatus;
    }

    public DirectorRequestException(Throwable cause, HttpStatus codeStatus) {
        super(cause);
        this.codeStatus = codeStatus;
    }

    public DirectorRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus codeStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.codeStatus = codeStatus;
    }

    public DirectorRequestException() {
    }

    public DirectorRequestException(String message) {
        super(message);
    }


    public DirectorRequestException(String message, Throwable cause) {
        super(message, cause);

    }

    public DirectorRequestException(Throwable cause) {
        super(cause);
    }

    public DirectorRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
