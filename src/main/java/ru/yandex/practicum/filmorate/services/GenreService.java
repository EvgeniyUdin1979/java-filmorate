package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storages.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    GenreStorage storage;

    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }


    public List<Genre> findAll() {
        return storage.findAll();
    }

    public Genre findById(int id) {
        Genre genre = storage.findById(id);
        if (genre == null) {
            throw new FilmRequestException("Жанра с id " + id + "не существует!", HttpStatus.NOT_FOUND);
        }
        return genre;
    }
}
