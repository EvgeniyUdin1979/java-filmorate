package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage storage;

    public List<Director> findAll() {
        return storage.findAll();
    }

    public Director findById(String userId) {
        int id = validateAndParseInt(userId);
        Director director = storage.findById(id);
        if (director == null) {
            // Я думаю можно оставить FilmRequestException, не создавая новую ошибку.
            throw new FilmRequestException("Режиссёра с данным id " + id + " не существует!", HttpStatus.NOT_FOUND);
        }
        return director;
    }

    public Director updateDirector(Director director) {
        if (director.getId() == 0) {
            String message = "Для обновления режиссёра id нужно указать больше чем 0!";
            log.info(message, director);
            throw new FilmRequestException(message);
        }
        findById(String.valueOf(director.getId()));
        return storage.update(director);
    }

    public Director createDirector(Director director) {
        return storage.create(director);
    }

    public void deleteById(String userId) {
        int id = validateAndParseInt(userId);
        findById(String.valueOf(id));
        storage.removeById(id);
    }

    public void removeAll() {
        storage.removeAll();
    }

    private int validateAndParseInt(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            String message = String.format("Данный id: %s, не целое число!", id);
            log.info(message);
            throw new FilmRequestException(message, HttpStatus.BAD_REQUEST);
        }
    }
}
