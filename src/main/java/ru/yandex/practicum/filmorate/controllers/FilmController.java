package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controllers.repositories.FilmsRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    @Autowired
    FilmsRepository films;

    @GetMapping
    public List<Film> getAllFilms(){
        log.info("Получены данные по всем фильмам.");
        return films.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") int id){
        log.info(String.format("Получены данные по фильмy id: %d.",id));
        return films.getFilm(id);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film){
        films.addFilm(film);
        log.info("Фильм добавлен.");
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film){
        films.updateFilm(film);
        log.info("Данные по фильму обновленны.");
        return film;
    }

}
