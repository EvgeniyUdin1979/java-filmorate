package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Film findById(int id);

    Film create(Film film);

    void removeById(int id);

    Film update(Film film);

    void removeAll();

    List<Film> getFilmBySearch(String query, String by);

    boolean exists(int id);

    List<Film> getFilmsByDirector(int directorId, Optional<String> sortBy);
}
