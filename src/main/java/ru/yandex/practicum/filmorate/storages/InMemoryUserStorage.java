package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
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
        user.setId(getGlobalId());
        users.put(user.getId(), user);
    }

    @Override
    public void removeById(int id) {
        users.remove(id);
    }

    public void update(User user) {
        users.put(user.getId(), user);
    }

    public User findById(int id) {
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void removeAll(){
        users.clear();
        globalId = 0;
    }



    private int getGlobalId() {
        return ++globalId;
    }
}
