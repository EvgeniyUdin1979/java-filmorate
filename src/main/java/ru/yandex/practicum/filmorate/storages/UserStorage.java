package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(int id);

    void create(User user);

    void removeById(int id);

    void update(User user);

    void removeAll();
}
