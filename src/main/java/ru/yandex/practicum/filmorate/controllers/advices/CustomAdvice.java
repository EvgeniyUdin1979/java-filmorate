package ru.yandex.practicum.filmorate.controllers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;

@ControllerAdvice
public class CustomAdvice {

    @ExceptionHandler(FilmRequestException.class)
    public ResponseEntity<Response> handleFilmException(FilmRequestException re) {
        return getResponse(re.getCodeStatus(), re.getMessage());
    }

    @ExceptionHandler(UserRequestException.class)
    public ResponseEntity<Response> handleUserException(UserRequestException re) {
        return getResponse(re.getCodeStatus(), re.getMessage());
    }

    private ResponseEntity<Response> getResponse(HttpStatus httpStatus, String message) {
        Response response = new Response(message);
        HttpStatus status;
        if (httpStatus != null) {
            status = httpStatus;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }
}
