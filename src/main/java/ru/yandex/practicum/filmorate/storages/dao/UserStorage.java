package ru.yandex.practicum.filmorate.storages.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(int id);

    User create(User user);

    void removeById(int id);

    User update(User user);

    void removeAll();

    boolean isExists(int id);
}
