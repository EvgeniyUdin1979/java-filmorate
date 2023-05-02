package ru.yandex.practicum.filmorate.storages.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    void add(int userId, int friendId);

    void remove(int userId, int friendId);

    List<User> common(int userId, int friendId);

    List<Integer> findAllById(int id);
}
