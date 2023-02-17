package ru.yandex.practicum.filmorate.controllers.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class UsersRepository {
    private int id;
    private final HashMap<Integer, User> users;

    public UsersRepository() {
        this.users = new HashMap<>();
        this.id = 0;
    }

    public void addUser(User user) throws FilmRequestException {
        validate(user);
        if (user.getId() != 0){
            log.info("Для добавления пользователя не нужно указывать id"+ " : " + user);
            throw new UserRequestException("Для добавления пользователя не нужно указывать id");
        }
        user.setId(getId());
        users.put(user.getId(), user);
    }

    public void updateUser(User user) {
        validate(user);
        if (!users.containsKey(user.getId())){
            log.info(String.format("Пользователь с данным id: %d, не найден",user.getId()),user);
            throw new UserRequestException(String.format("Пользователь с данным id: %d, не найден",user.getId()), HttpStatus.NOT_FOUND);
        }
        users.put(user.getId(), user);
    }

    public User getUser(int id) {
        if (!users.containsKey(id)) {
            log.info(String.format("Пользователь с данным id: %d, не найден", id));
            throw new UserRequestException(String.format("Пользователь с данным id: %d, не найден", id), HttpStatus.NOT_FOUND);
        }
            return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void validate(User user) throws FilmRequestException {
        if (!Pattern.matches("^(?=.{1,64}@)[A-Za-z0-9_\\-]+(\\\\.[A-Za-z0-9_\\-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", user.getEmail())) {
            log.info("Не корректный Email!" + " : " + user);
            throw new UserRequestException("Не корректный Email!");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.info("Login не может быть пустым или отсутствовать!" + " : " + user);
            throw new UserRequestException("Login не может быть пустым или отсутствовать!");
        } else if (user.getLogin().contains(" ")) {
            log.info("Login не может содержать пробелы" + " : " + user);
            throw new UserRequestException("Login не может содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Дата рождения не может отсутствовать или быть в будущем!" + " : " + user);
            throw new UserRequestException("Дата рождения не может отсутствовать или быть в будущем!");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User не имеет имени!" + " : " + user);
            user.setName(user.getLogin());
        }
    }

    private int getId() {
        return ++id;
    }
}
