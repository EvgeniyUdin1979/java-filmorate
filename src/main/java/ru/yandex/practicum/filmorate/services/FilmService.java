package ru.yandex.practicum.filmorate.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.LikesStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {


    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDAO") FilmStorage filmStorage, LikesStorage likesStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
        this.userService = userService;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(String id) {
        return findFilmById(validateAndParseInt(id));
    }

    public Film add(Film film) {
        if (film.getId() != 0) {
            String message = String.format("Для добавления фильма не нужно указывать id. Текущий id: %d", film.getId());
            log.info(message, film);
            throw new FilmRequestException(message);
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        findFilmById(film.getId());
        return filmStorage.update(film);
    }

    public void removeAll() {
        filmStorage.removeAll();
    }

    public void addLike(String userId, String filmId) {
        int film = validateAndParseInt(filmId);
        int user = validateAndParseInt(userId);
        findFilmById(film);
        findById(userId);
        likesStorage.add(user,film);
    }

    public void removeLike(String userId, String filmId) {
        int film = validateAndParseInt(filmId);
        int user = validateAndParseInt(userId);
        findFilmById(film);
        findById(userId);
        likesStorage.remove(user,film);
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

    private int validateAndParseInt(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            String message = String.format("Данный id: %s, не целое число!", id);
            log.info(message);
            throw new FilmRequestException(message, HttpStatus.BAD_REQUEST);
        }
    }

    private Film findFilmById(int id) {
        Film user = filmStorage.findById(id);
        if (user == null) {
            String message = String.format("Фильм с данным id: %d, не найден", id);
            log.info(message);
            throw new FilmRequestException(message, HttpStatus.NOT_FOUND);
        }
        return user;
    }
}
