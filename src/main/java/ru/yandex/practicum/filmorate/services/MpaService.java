package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage storage;

    public List<Mpa> findAll() {
        return storage.findAll();
    }

    public Mpa findById(int id) {
        Mpa mpa = storage.findById(id);
        if (mpa == null) {
            throw new FilmRequestException("Рейтинга с данным id " + id + "  не существует!", HttpStatus.NOT_FOUND);
        }
        return mpa;
    }
}
