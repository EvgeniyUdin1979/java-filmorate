package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.errors.FilmRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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

    public void create(Film film) throws FilmRequestException{
        validate(film);
        if (film.getId() != 0){
            log.info(String.format("Для добавления фильма не нужно указывать id. Текущий id: %d",film.getId()),film);
            throw new FilmRequestException(String.format("Для добавления фильма не нужно указывать id. Текущий id: %d",film.getId()));
        }
        film.setId(getGlobalId());
        films.put(film.getId(),film );
    }

    public void update(Film film){
        validate(film);
        if (!films.containsKey(film.getId())) {
            log.info(String.format("Фильм с данным id: %d, не найден",film.getId()),film);
            throw new FilmRequestException(String.format("Фильм с данным id: %d, не найден!",film.getId()),HttpStatus.NOT_FOUND);
        }
        films.put(film.getId(), film);
    }

    public Film findById(String stringId){
        int id = validateId(stringId);
        Film film = films.get(id);
        if (film == null){
            log.info(String.format("Фильм с данным id: %d, не найден",id),id);
            throw new FilmRequestException(String.format("Фильм с данным id: %d, не найден!",id),HttpStatus.NOT_FOUND);
        }
        return film;
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

    private int validateId(String id) {
        try{
            return Integer.parseInt(id);
        }catch (NumberFormatException e){
            log.info(String.format("Данный id: %s, не целое число!",id));
            throw new FilmRequestException(String.format("Данный id: %s, не целое число!",id),HttpStatus.BAD_REQUEST);
        }
    }

    private void validate(Film film) throws FilmRequestException {
        LocalDate firstReleaseDate = LocalDate.of(1895,12,28);
        if (film.getReleaseDate().isBefore(firstReleaseDate)){
            log.info("Дата релиза не может быть раньше 28.12.1895 года!" + " : " + film);
            throw new FilmRequestException("Дата релиза не может быть раньше 28.12.1895 года!");
        }

    }

    private int getGlobalId() {
        return ++globalId;
    }

}
