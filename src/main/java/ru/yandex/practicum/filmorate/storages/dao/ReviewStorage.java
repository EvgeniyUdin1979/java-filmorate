package ru.yandex.practicum.filmorate.storages.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review save(Review review);

    boolean exists(int id);

    Review update(Review review);

    void delete(int id);

    Review find(int id);

    List<Review> findAll(int count);

    List<Review> findAllByFilmId(int filmId, int count);

    void addLike(int id, int userId);

    int deleteDislike(int id, int userId);

    int deleteLike(int id, int userId);

    void addDislike(int id, int userId);
}
