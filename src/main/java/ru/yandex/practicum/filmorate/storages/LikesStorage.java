package ru.yandex.practicum.filmorate.storages;

public interface LikesStorage {

    void add(int userId, int filmId);

    void remove(int userId, int filmId);
}
