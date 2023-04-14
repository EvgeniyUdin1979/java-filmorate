package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public ResponseEntity<Review> addReview(@Valid @RequestBody Review review) {
        return ResponseEntity.status(201).body(service.saveReview(review));
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        return ResponseEntity.ok().body(service.updateReview(review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") int id) {
        return ResponseEntity.ok().body(service.deleteReview(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable("id") int id) {
        return ResponseEntity.ok().body(service.getReview(id));
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@RequestParam(value = "filmId", required = false) String filmId,
                                                   @RequestParam(value = "count", defaultValue = "10") String count) {
        return ResponseEntity.ok().body(service.getReviews(filmId, count));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<String> addLike(@PathVariable("id") int id,
                                          @PathVariable("userId") int userId) {
        return ResponseEntity.ok().body(service.addLike(id, userId));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<String> addDislike(@PathVariable("id") int id,
                                             @PathVariable("userId") int userId) {
        return ResponseEntity.ok().body(service.addDislike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<String> deleteLike(@PathVariable("id") int id,
                                             @PathVariable("userId") int userId) {
        return ResponseEntity.ok().body(service.deleteLike(id, userId));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<String> deleteDislike(@PathVariable("id") int id,
                                                @PathVariable("userId") int userId) {
        return ResponseEntity.ok().body(service.deleteDislike(id, userId));
    }

}
