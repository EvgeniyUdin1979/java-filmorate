package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int globalId;
    private final HashMap<Integer, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
        this.globalId = 0;
    }

    public void create(User user) throws FilmRequestException {
        if (user.getId() != 0) {
            log.info("Для добавления пользователя не нужно указывать id" + " : " + user);
            throw new UserRequestException("Для добавления пользователя не нужно указывать id");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User не имеет имени! Будет использован login" + " : " + user);
            user.setName(user.getLogin());
        }
        user.setId(getGlobalId());
        users.put(user.getId(), user);
    }

    @Override
    public void removeById(String stringId) {
        int id = validateId(stringId);
        searchById(id);
        users.remove(id);
    }

    public void update(User user) {
        searchById(user.getId());
        users.put(user.getId(), user);
    }

    public User findById(String stringId) {
        int id = validateId(stringId);
        searchById(id);
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void removeAll(){
        users.clear();
        globalId = 0;
    }

    private void searchById(int id) {
        if (!users.containsKey(id)) {
            log.info(String.format("Пользователь с данным id: %d, не найден", id));
            throw new UserRequestException(String.format("Пользователь с данным id: %d, не найден", id), HttpStatus.NOT_FOUND);
        }
    }

    private int validateId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            log.info(String.format("Данный id: %s, не целое число!", id));
            throw new FilmRequestException(String.format("Данный id: %s, не целое число!", id), HttpStatus.BAD_REQUEST);
        }
    }

    private int getGlobalId() {
        return ++globalId;
    }
}
