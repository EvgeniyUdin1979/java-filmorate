package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storages.dao.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final EventService eventService;

    public Review saveReview(Review review) {
        int userId = review.getUserId();
        int filmId = review.getFilmId();
        userService.findUserById(userId);
        filmService.findFilmById(filmId);
        review = reviewStorage.save(review);
        int reviewId = review.getReviewId();
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(reviewId)
                .build());
        log.info(String.format("Отзыв с id(%d) успешно сохранен!", reviewId));
        return review;
    }

    public Review updateReview(Review review) {
        int userId = review.getUserId();
        int filmId = review.getFilmId();
        int reviewId = review.getReviewId();
        findReviewById(reviewId);
        filmService.findFilmById(filmId);
        userService.findUserById(userId);
        review = reviewStorage.update(review);
        eventService.addEvent(Event.builder()
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(review.getReviewId())
                .build());
        log.info(String.format("Отзыв с id(%d) успешно обновлен!", reviewId));
        return review;
    }

    public String deleteReview(int id) {
        findReviewById(id);
        Review review = findById(id);
        reviewStorage.delete(id);
        int deleteReviewId = review.getReviewId();
        int userId = review.getUserId();
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .entityId(deleteReviewId)
                .build());
        String message = String.format("Отзыв с id(%d) успешно удален!", id);
        log.info(message);
        return message;
    }

    public Review findById(int id) {
        findReviewById(id);
        return reviewStorage.find(id);
    }

    public List<Review> getReviews(Integer filmId, Integer num) {
        if (filmId == null) {
            return reviewStorage.findAll(num);
        } else {
            filmService.findFilmById(filmId);
            return reviewStorage.findAllByFilmId(filmId, num);
        }
    }

    public String addLike(int id, int userId) {
        findReviewById(id);
        userService.findUserById(userId);
        reviewStorage.addLike(id, userId);
        String message = String.format("Пользователь с id(%d) успешно лайкнул отзыв с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    public String addDislike(int id, int userId) {
        findReviewById(id);
        userService.findUserById(userId);
        reviewStorage.addDislike(id, userId);
        String message = String.format("Пользователь с id(%d) успешно дизлайкнул отзыв с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    public String deleteLike(int id, int userId) {
        findReviewById(id);
        userService.findUserById(userId);
        if (reviewStorage.deleteLike(id, userId) == 0) {
            throw new NotFoundException(
                    String.format("У отзыва с id(%d) не найден лайк от пользователя с id(%d)!", userId, id));
        }
        String message = String.format("Пользователь с id(%d) успешно удалил лайк у отзыва с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    public String deleteDislike(int id, int userId) {
        findReviewById(id);
        userService.findUserById(userId);
        if (reviewStorage.deleteDislike(id, userId) == 0) {
            throw new NotFoundException(
                    String.format("У отзыва с id(%d) не найден дизлайк от пользователя с id(%d)!", userId, id));
        }
        String message = String.format("Пользователь с id(%d) успешно удалил дизлайк у отзыва с id(%d)!", userId, id);
        log.info(message);
        return message;
    }

    private void findReviewById(int id) {
        if (!reviewStorage.exists(id)) {
            throw new NotFoundException(String.format("Отзыв с id(%d) не найден!", id));
        }
    }
}
