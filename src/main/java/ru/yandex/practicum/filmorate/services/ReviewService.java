package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storages.ReviewStorage;
import ru.yandex.practicum.filmorate.storages.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storages.dao.UserDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage storage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    public Review saveReview(Review review) {
        validateUserNotExists(review.getUserId());
        validateFilmNotExists(review.getFilmId());
        review = storage.save(review);
        log.info(String.format("Отзыв с id(%d) успешно сохранен!", review.getReviewId()));
        return review;
    }

    public Review updateReview(Review review) {
        validateReviewNotExists(review.getReviewId());
        validateFilmNotExists(review.getFilmId());
        validateUserNotExists(review.getUserId());
        review = storage.update(review);
        log.info(String.format("Отзыв с id(%d) успешно обновлен!", review.getReviewId()));
        return review;
    }

    public String deleteReview(int id) {
        validateReviewNotExists(id);
        storage.delete(id);
        String message = String.format("Отзыв с id(%d) успешно удален!", id);
        log.info(message);
        return message;
    }

    public Review getReview(int id) {
        validateReviewNotExists(id);
        return storage.find(id);
    }

    public List<Review> getReviews(String filmId, String num) {
        if (filmId == null || filmId.isBlank()) {
            return storage.findAll(Integer.parseInt(num));
        } else {
            validateFilmNotExists(Integer.parseInt(filmId));
            return storage.findAllByFilmId(Integer.parseInt(filmId), Integer.parseInt(num));
        }
    }

    public String addLike(int id, int userId) {
        validateReviewNotExists(id);
        validateUserNotExists(userId);
        storage.addLike(id, userId);
        String message = String.format("Пользователь с id(%d) успешно лайкнул отзыв с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    public String addDislike(int id, int userId) {
        validateReviewNotExists(id);
        validateUserNotExists(userId);
        storage.addDislike(id, userId);
        String message = String.format("Пользователь с id(%d) успешно дизлайкнул отзыв с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    public String deleteLike(int id, int userId) {
        validateReviewNotExists(id);
        validateUserNotExists(userId);
        if (storage.deleteLike(id, userId) == 0) {
            throw new NotFoundException(
                    String.format("У отзыва с id(%d) не найден лайк от пользователя с id(%d)!", userId, id));
        }
        String message = String.format("Пользователь с id(%d) успешно удалил лайк у отзыва с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    public String deleteDislike(int id, int userId) {
        validateReviewNotExists(id);
        validateUserNotExists(userId);
        if (storage.deleteDislike(id, userId) == 0) {
            throw new NotFoundException(
                    String.format("У отзыва с id(%d) не найден дизлайк от пользователя с id(%d)!", userId, id));
        }
        String message = String.format("Пользователь с id(%d) успешно удалил дизлайк у отзыва с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    private void validateReviewNotExists(int id) {
        if (!storage.exists(id)) {
            throw new NotFoundException(String.format("Отзыв с id(%d) не найден!", id));
        }
    }

    private void validateUserNotExists(int id) {
        if (!userDbStorage.exists(id)) {
            throw new NotFoundException(String.format("Пользователь с id(%d) не найден!", id));
        }
    }

    private void validateFilmNotExists(int id) {
        if (!filmDbStorage.exists(id)) {
            throw new NotFoundException(String.format("Фильм с id(%d) не найден!", id));
        }
    }
}
