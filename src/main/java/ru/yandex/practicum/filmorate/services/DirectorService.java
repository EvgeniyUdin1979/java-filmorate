package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {
    @Autowired
    DirectorStorage storage;

    public List<Director> findAll() {
        return storage.findAll();
    }

    public Director findById(int id) {
        Director director = storage.findById(id);
        if (director == null) {
            // Я думаю можно оставить FilmRequestException, не создавая новую ошибку.
            throw new FilmRequestException("Режиссёра с данным id " + id + "  не существует!", HttpStatus.NOT_FOUND);
        }
        return director;
    }

    public Director updateDirector(Director director) {
        if (director.getId() == 0) {
            String message = "Для обновления режиссёра id нужно указать больше чем 0!";
            log.info(message, director);
            throw new FilmRequestException(message);
        }
        findById(director.getId());
        return storage.update(director);
    }

    public Director createDirector(Director director) {
        if (director.getName() == null || director.getName().isEmpty() || director.getName().isBlank()) {
            throw new FilmRequestException("Имя режиссёра пустое!", HttpStatus.BAD_REQUEST);
        }
        return storage.create(director);
    }

    public void deleteById(String userId) {
        int id = validateAndParseInt(userId);
        findById(id);
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
