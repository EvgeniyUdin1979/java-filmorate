package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{
    private int globalId;
    private final HashMap<Integer, Film> films;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.globalId = 0;
    }

    public Film create(Film film) throws FilmRequestException{
        film.setId(getGlobalId());
        films.put(film.getId(),film );
        return film;
    }

    public void update(Film film){
        films.put(film.getId(), film);
    }

    public Film findById(int id){
        return films.get(id);
    }


    public void removeById(int id){
        films.remove(id);
    }

    public List<Film> findAll(){
        return new ArrayList<>(films.values());
    }

    public void removeAll(){
        films.clear();
        globalId = 0;
    }

    private int getGlobalId() {
        return ++globalId;
    }

}
