package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Director {
    private int id;
    @NotBlank(message = "Имя режиссёра не должно быть пустым.")
    private String name;
}
