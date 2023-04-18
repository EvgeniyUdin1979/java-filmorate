package ru.yandex.practicum.filmorate.validate;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Slf4j
public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {
    @Override
    public void initialize(ReleaseDateConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            String message = "Дата релиза фильма не может отсутствовать!";
            log.info(message);
            throw new FilmRequestException(message);
        }
        /*
        if (value.isAfter(LocalDate.now())) {
            String message = "Дата релиза не может быть в будущем!";
            log.info(message);
            throw new FilmRequestException(message);
        }
        Тут в тестах постмана есть фильмы из "будущего", так что я так сделаю пока.
         */
        LocalDate firstReleaseDate = LocalDate.of(1895, 12, 28);
        if (value.isBefore(firstReleaseDate)) {
            String message = "Дата релиза не может быть раньше 28.12.1895 года!";
            log.info(message);
            throw new FilmRequestException(message);
        }
        return true;
    }
}
