package ru.yandex.practicum.filmorate.controllers.repositories;

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
public class FilmsRepository {
    private int id;
    private final HashMap<Integer, Film> films;

    public FilmsRepository() {
        this.films = new HashMap<>();
        this.id = 0;
    }

    public void addFilm(Film film) throws FilmRequestException{
        validate(film);
        if (film.getId() != 0){
            log.info(String.format("Для добавления фильма не нужно указывать id. Текущий id: %d",film.getId()),film);
            throw new FilmRequestException(String.format("Для добавления фильма не нужно указывать id. Текущий id: %d",film.getId()));
        }
        film.setId(getId());
        films.put(film.getId(),film );
    }

    public void updateFilm(Film film){
        validate(film);
        if (!films.containsKey(film.getId())) {
            log.info(String.format("Фильм с данным id: %d, не найден",film.getId()),film);
            throw new FilmRequestException(String.format("Фильм с данным id: %d, не найден!",film.getId()),HttpStatus.NOT_FOUND);
        }
        films.put(film.getId(), film);
    }

    public Film getFilm(int id){
        return films.get(id);
    }

    public List<Film> getAllFilms(){
        return new ArrayList<>(films.values());
    }
    private void validate(Film film) throws FilmRequestException {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()){
            log.info("Название фильма не может быть пустым или отсутствовать!" + " : " + film);
            throw new FilmRequestException("Название фильма не может быть пустым или отсутствовать!");
        }else if (film.getDescription() != null && film.getDescription().length() > 200){
            log.info("Описание фильма не должно превышать 200 символов!" + " : " + film);
            throw new FilmRequestException("Описание фильма не должно превышать 200 символов!");
        }
        if (film.getReleaseDate() == null){
            log.info("Дата выхода не может отсутствовать!" + " : " + film);
            throw new FilmRequestException("Дата выхода не может отсутствовать!");
        }else {
            LocalDate firstReleaseDate = LocalDate.of(1895,12,28);
            if (film.getReleaseDate().isBefore(firstReleaseDate)){
                log.info("Дата релиза не может быть раньше 28.12.1895 года!" + " : " + film);
                throw new FilmRequestException("Дата релиза не может быть раньше 28.12.1895 года!");
            }
        }
        if (film.getDuration() < 1){
            log.info("Длительность фильма должна быть больше чем 0!" + " : " + film);
            throw new FilmRequestException("Длительность фильма должна быть больше чем 0!");
        }
    }

    private int getId() {
        return ++id;
    }

}
