package ru.yandex.practicum.filmorate.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {


    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(String userId,String filmId){
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        film.getLikesId().add(user.getId());
        film.setLikesQuantity(film.getLikesId().size());
    }

    public void removeLike(String userId,String filmId){
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        film.getLikesId().remove(user.getId());
        film.setLikesQuantity(film.getLikesId().size());
    }

    public List<Film> mostPopularFilm(int count){
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> o2.getLikesId().size() - o1.getLikesId().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> mostPopularFilm(){
        return mostPopularFilm(10);
    }


}
