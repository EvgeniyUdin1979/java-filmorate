package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director findById(int id);

    Director create(Director director);

    void removeById(int id);

    Director update(Director director);

    void removeAll();
}
