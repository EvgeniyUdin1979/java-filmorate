package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/films")
public class FilmController {

    private final FilmService service;

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получены данные по всем фильмам.");
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable("id") String id) {
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
    public void deleteFilm(@PathVariable("id") String id) {
        service.deleteById(id);
        log.info("Удален фильм id {}", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") String id,
                        @PathVariable("userId") String userId) {
        service.addLike(userId, id);
        log.info("Добавлен лайк фильму {} от пользователя {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") String id,
                           @PathVariable("userId") String userId) {
        service.removeLike(userId, id);
        log.info("Удален лайк фильму {} от пользователя {}", id, userId);
    }

    @DeleteMapping("/resetDB")
    public void reset() {
        log.info("FilmStorage очищена.");
        service.removeAll();
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirector(@PathVariable("directorId") String directorId,
                                    @RequestParam Optional<String> sortBy) {
        return service.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getMostPopular(
            @RequestParam(name = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        return ResponseEntity.ok().body(service.getMostPopular(count, genreId, year));
    }


    @GetMapping("/search")
    public List<Film> getFilmBySearch(@RequestParam("query") @NotNull String query,
                                       @RequestParam("by") @NotNull String by) {
        log.info("Поиск самых популярных фильмов по запросу {}.", query);
        return service.getFilmBySearch(query, by);
    }
}
