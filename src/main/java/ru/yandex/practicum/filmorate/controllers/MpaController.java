package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.services.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    MpaService service;

    @Autowired
    public MpaController(MpaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Получены все рейтинги.");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("Получены все рейтинги.");
        return service.findById(id);
    }
}
