package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties(value = "friendsId")
public class User {
    @PositiveOrZero(message = "id не может быть отрицательный")
    private int id;
    @Email(message = "Не корректный Email!")
    private final String email;

    //    @NotBlank(message = "Логин не может быть пустым, отсутствовать или состоять только из пробелов!")
    @Pattern(regexp = "^\\w+$", message = "Логин может состоять только из латинских букв и подчеркивания. Логин не может быть пустым, отсутствовать или состоять только из пробелов!")
    private final String login;
    private String name;

    @NotNull(message = "Дата рождения не может отсутствовать!")
    @Past(message = "Дата рождения должна быть в прошлом!")
    private final LocalDate birthday;

    private final Set<Integer> friendsId = new HashSet<>();

    @JsonCreator
    public User(@JsonProperty("id") int id,
                @JsonProperty("email") String email,
                @JsonProperty("login") String login,
                @JsonProperty("name") String name,
                @JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
