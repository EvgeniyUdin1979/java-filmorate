package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> findAll();

    Genre findById(int id);
}
