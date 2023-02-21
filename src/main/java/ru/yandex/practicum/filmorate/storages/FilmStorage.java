package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film findById(String id);

    void create(Film film);

    void removeById(int id);

    void update(Film film);

    void removeAll();
}
