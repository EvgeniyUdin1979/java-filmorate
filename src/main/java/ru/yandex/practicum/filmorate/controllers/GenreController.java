package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.services.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService service;

    @GetMapping
    public List<Genre> getAll() {
        List<Genre> genres = service.findAll();
        log.info("Все жанры получены.");
        return genres;
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable int id) {
        Genre genre = service.findById(id);
        log.info("Получен жанр {}", genre.getName());
        return genre;
    }
}
