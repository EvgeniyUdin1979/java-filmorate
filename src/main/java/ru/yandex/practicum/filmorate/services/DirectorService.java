package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.DirectorRequestException;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storages.dao.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }


    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(int id) {
        findDirectorById(id);
        return directorStorage.findById(id);
    }

    public Director updateDirector(Director director) {
        int id = director.getId();
        if (id == 0) {
            String message = String.format("Для обновления режиссёра id нужно указать больше чем 0! %s", director);
            log.info(message);
            throw new FilmRequestException(message);
        }
        findDirectorById(id);
        return directorStorage.update(director);
    }

    public Director createDirector(Director director) {
//        if (director.getId() != 0) {
//            String message = String.format("Для создания режиссёра id не нужно указать! %s", director);
//            log.info(message);
//            throw new DirectorRequestException(message);
//        }
        return directorStorage.create(director);
    }

    public void deleteById(int id) {
        findDirectorById(id);
        directorStorage.removeById(id);
    }

    public void findDirectorById(int id) {
        if (!directorStorage.isExists(id)) {
            String message = String.format("Режиссёра с данным id %d не существует!", id);
            log.info(message);
            throw new DirectorRequestException(message, HttpStatus.NOT_FOUND);
        }
    }

    public void removeAll() {
        directorStorage.removeAll();
    }
}
