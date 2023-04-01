package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film findById(int id);

    Film create(Film film);

    void removeById(int id);

    Film update(Film film);

    void removeAll();
}
