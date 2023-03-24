package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    MpaStorage storage;

    @Autowired
    public MpaService(MpaStorage storage) {
        this.storage = storage;
    }

    public List<Mpa> findAll() {
        return storage.findAll();
    }

    public Mpa findById(int id) {
        Mpa mpa = storage.findById(id);
        if (mpa == null){
            throw new FilmRequestException("Рейтинга с данным id " + id +"  не существует!", HttpStatus.NOT_FOUND);
        }
        return mpa;
    }
}
