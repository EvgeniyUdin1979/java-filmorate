package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/directors")
public class DirectorController {


    private final DirectorService service;

    @GetMapping
    public List<Director> getAll() {
        log.info("Получены данные по всем режиссёрам.");
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public Director getDirectorById(@PathVariable("id") String id) {
        Director director = service.findById(id);
        log.info(String.format("Получены данные по режиссёру id: %s.", id));
        return director;
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director) {
        director = service.createDirector(director);
        log.info("Директор добавлен.");
        return director;
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        director = service.updateDirector(director);
        log.info(String.format("Обновлены данные по режиссёру id: %s.", director.getId()));
        return director;
    }

    @DeleteMapping(value = "/{id}")
    public void deleteDirector(@PathVariable("id") String id) {
        service.deleteById(id);
        log.info(String.format("Удалены данные по режиссёру id: %s.", id));
    }

    @DeleteMapping("/resetDB")
    public void reset() {
        log.info("DirectorStorage очищена.");
        service.removeAll();
    }

    @Autowired
    public DirectorController(DirectorService service) {
        this.service = service;
    }
}
