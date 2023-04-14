package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    int reviewId;
    @NotBlank(message = "Отзыв не может быть пустым или состоять только из пробелов!")
    String content;
    @NotNull(message = "Тип отзыва должен быть указан!")
    Boolean isPositive;
    @NotNull(message = "Id пользователя должен быть указан!")
    Integer userId;
    @NotNull(message = "Id фильма должен быть указан!")
    Integer filmId;
    int useful;

}
