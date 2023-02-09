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
    public ResponseEntity<Response> handleFilmException(FilmRequestException re){
        Response response = new Response(re.getMessage());
        HttpStatus status;
        if (re.getCodeStatus() != null ){
            status = re.getCodeStatus();
        }else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(UserRequestException.class)
    public ResponseEntity<Response> handleUserException(UserRequestException re){
        Response response = new Response(re.getMessage());
        HttpStatus status;
        if (re.getCodeStatus() != null ){
            status = re.getCodeStatus();
        }else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }
}
