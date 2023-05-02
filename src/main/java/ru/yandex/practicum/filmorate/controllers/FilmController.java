package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получены данные по всем фильмам.");
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable("id") int id) {
        Film film = service.findById(id);
        log.info(String.format("Получены данные по фильмy id: %s.", id));
        return film;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        film = service.add(film);
        log.info("Фильм добавлен.");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film filmUpdate = service.update(film);
        log.info("Данные по фильму обновленны.");
        return filmUpdate;
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") int id) {
        service.deleteById(id);
        log.info("Удален фильм id {}", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("userId") int userId) {
        service.addLike(userId, id);
        log.info("Добавлен лайк фильму {} от пользователя {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int id,
                           @PathVariable("userId") int userId) {
        service.removeLike(userId, id);
        log.info("Удален лайк фильму {} от пользователя {}", id, userId);
    }

    @GetMapping("/popular")
    public List<Film> popularFilms(@RequestParam Optional<Integer> count) {
        if (count.isPresent()) {
            try {
                int c = count.get();
                if (c < 1) {
                    String messageError = "Параметр count должен быть больше 0 или отсутствовать!";
                    log.info(messageError);
                    throw new UserRequestException(messageError, HttpStatus.BAD_REQUEST);
                }
                List<Film> mostPopularFilms = service.mostPopularFilm(c);
                log.info("Получены популярные фильмы, колличество {}.", c);
                return mostPopularFilms;
            } catch (NumberFormatException e) {
                String messageError = "Параметр count не является натуральным числом! " + e.getMessage();
                log.info(messageError);
                throw new UserRequestException(messageError, HttpStatus.BAD_REQUEST);
            }
        } else {
            List<Film> mostPopularFilms = service.mostPopularFilm();
            log.info("Получены популярные фильмы.");
            return mostPopularFilms;
        }
    }

    @DeleteMapping("/resetDB")
    public void reset() {
        log.info("FilmStorage очищена.");
        service.removeAll();
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirector(@PathVariable("directorId") int directorId,
                                    @RequestParam Optional<String> sortBy) {
        return service.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getFilmBySearch(@RequestParam("query")
                                      @NotBlank(message = "Текст для поиска не может отсутствовать или быть пустым")
                                      String query,
                                      @RequestParam("by")
                                      @NotBlank(message = "Текст для сортировки не может отсутствовать или быть пустым")
                                      String by) {
        log.info("Поиск самых популярных фильмов по запросу {}.", query);
        return service.getFilmBySearch(query, by);
    }
}
