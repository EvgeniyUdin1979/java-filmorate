package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validate.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(value = {"likesQuantity"}, allowGetters = true)
public class Film {
    @PositiveOrZero(message = "id не может быть отрицательный")
    private int id;

    @NotBlank(message = "Название фильма не может быть отсутствовать, быть пустым или состоять только из пробелов!")
    private final String name;

    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов!")
    private final String description;
    @ReleaseDateConstraint
    private final LocalDate releaseDate;

    @Positive(message = "Длительность фильма должна быть больше чем 0!")
    private final int duration;
    @JsonIgnore
    private final Set<Integer> likesId = new HashSet<>();

    @JsonIgnoreProperties(allowGetters = true)
    private int likesQuantity;

    private Mpa mpa;

    private final Set<Genre> genres = new HashSet<>();

    @JsonCreator
    public Film(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("releaseDate") LocalDate releaseDate,
                @JsonProperty("duration") int duration,
                @JsonProperty("likesQuantity") int likesQuantity,
                @JsonProperty("mpa") Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesQuantity = likesQuantity;
        this.mpa = mpa;
    }


    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                '}';
    }
}
