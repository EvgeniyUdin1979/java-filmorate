package ru.yandex.practicum.filmorate.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storages.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storages.dao.LikesStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {


    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final UserService userService;
    private final EventService eventService;
    private final DirectorService directorService;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(int id) {
        findFilmById(id);
        return filmStorage.findById(id);
    }

    public Film add(Film film) {
        if (film.getId() != 0) {
            String message = String.format("Для добавления фильма не нужно указывать id. Текущий id: %d", film.getId());
            log.info(message);
            throw new FilmRequestException(message);
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() < 1) {
            String message = String.format("Для обновления фильма id должен быть больше 0. Текущий id: %d", film.getId());
            log.info(message);
            throw new FilmRequestException(message);
        }
        findFilmById(film.getId());
        return filmStorage.update(film);
    }

    public void deleteById(int id) {
        findFilmById(id);
        filmStorage.removeById(id);
    }

    public void removeAll() {
        filmStorage.removeAll();
    }

    public void addLike(int userId, int filmId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        likesStorage.add(userId, filmId);
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build());
    }

    public void removeLike(int userId, int filmId) {
        findFilmById(filmId);
        userService.findUserById(userId);
        likesStorage.remove(userId, filmId);
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build());
    }

    public List<Film> getFilmsByDirector(int directorId, Optional<String> sortBy) {
        directorService.findDirectorById(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> mostPopularFilm(int count) {
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> o2.getLikesQuantity() - o1.getLikesQuantity())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> mostPopularFilm() {
        return mostPopularFilm(10);
    }

    public List<Film> getFilmBySearch(String query, String by) {
        return filmStorage.getFilmBySearch(query, by);
    }

    public void findFilmById(int id) {
        if (!filmStorage.isExists(id)) {
            String message = String.format("Фильм с данным id: %d, не найден", id);
            log.info(message);
            throw new FilmRequestException(message, HttpStatus.NOT_FOUND);
        }
    }
}
