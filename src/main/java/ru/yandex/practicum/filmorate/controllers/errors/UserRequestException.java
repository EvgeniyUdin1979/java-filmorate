package ru.yandex.practicum.filmorate.controllers.errors;

import org.springframework.http.HttpStatus;

public class UserRequestException extends RuntimeException{
    private HttpStatus codeStatus;

    public HttpStatus getCodeStatus() {
        return codeStatus;
    }

    public UserRequestException(HttpStatus codeStatus) {
        this.codeStatus = codeStatus;
    }

    public UserRequestException(String message, HttpStatus codeStatus) {
        super(message);
        this.codeStatus = codeStatus;
    }

    public UserRequestException(String message, Throwable cause, HttpStatus codeStatus) {
        super(message, cause);
        this.codeStatus = codeStatus;
    }

    public UserRequestException(Throwable cause, HttpStatus codeStatus) {
        super(cause);
        this.codeStatus = codeStatus;
    }

    public UserRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus codeStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.codeStatus = codeStatus;
    }

    public UserRequestException() {
    }

    public UserRequestException(String message) {
        super(message);
    }

    public UserRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRequestException(Throwable cause) {
        super(cause);
    }

    public UserRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
