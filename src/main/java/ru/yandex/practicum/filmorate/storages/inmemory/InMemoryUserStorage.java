package ru.yandex.practicum.filmorate.storages.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component("inmemoryuser")
public class InMemoryUserStorage implements UserStorage {
    private int globalId;
    private final HashMap<Integer, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
        this.globalId = 0;
    }

    public User create(User user) throws FilmRequestException {
        user.setId(getGlobalId());
        users.put(user.getId(), user);
       return users.get(user.getId());
    }

    @Override
    public void removeById(int id) {
        users.remove(id);
    }

    public User update(User user) {
       return users.put(user.getId(), user);
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
