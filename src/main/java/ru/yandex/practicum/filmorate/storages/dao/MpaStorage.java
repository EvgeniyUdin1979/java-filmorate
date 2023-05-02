package ru.yandex.practicum.filmorate.storages.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> findAll();

    Mpa findById(int id);
}
