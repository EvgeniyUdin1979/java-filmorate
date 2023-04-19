package ru.yandex.practicum.filmorate.controllers.advices;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.controllers.errors.NotFoundException;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class CustomAdvice {

    @ExceptionHandler(FilmRequestException.class)
    public ResponseEntity<Response> handleFilmException(FilmRequestException re) {
        return getResponse(re.getCodeStatus(), re.getMessage());
    }

    @ExceptionHandler(UserRequestException.class)
    public ResponseEntity<Response> handleUserException(UserRequestException re) {
        return getResponse(re.getCodeStatus(), re.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleBindException(ConstraintViolationException cve) {
        List<ConstraintViolation<?>> constraintViolations = new ArrayList<>(cve.getConstraintViolations());
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            message.append(constraintViolation.getMessageTemplate()).append("; ");
        }
        log.info(message.toString().trim());
        return new ResponseEntity<>(new Response(message.toString().trim()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleException(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        Object target = ex.getBindingResult().getTarget();
        for (ObjectError error : ex.getAllErrors()) {
            message.append(error.getDefaultMessage()).append("; ");
        }
        message.append(target).append("; ");
        log.info(message.toString().trim());
        return new ResponseEntity<>(new Response(message.toString().trim()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<Response> handleException(JsonParseException ex) {
        String message = "Ошибка в полученном Json, проверьте данные и повторите попытку!";
        log.info(message);
        return new ResponseEntity<>(new Response(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleException(NotFoundException ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(new Response(ex.getMessage()), HttpStatus.NOT_FOUND);
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
