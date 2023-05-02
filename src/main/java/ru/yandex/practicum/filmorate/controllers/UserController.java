package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получены данные всех пользователей.");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") int id) {
        User user = service.findById(id);
        log.info(String.format("Получены данные по пользователю id: %s.", id));
        return user;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        User userAdd = service.createUser(user);
        log.info("Пользователь добавлен: " + userAdd);
        return userAdd;
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable int id,
            @PathVariable int friendId) {
        service.addFriend(id, friendId);
        log.info("Пользователь {} и пользователь {} теперь друзья.", id, friendId);

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        User userUpdate = service.updateUser(user);
        log.info("Данные пользователя обновлены.");
        return userUpdate;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        service.deleteById(id);
        log.info("Удален пользователь id {}", id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        service.removeFriend(id, friendId);
        log.info("Пользователь {} и пользователь {} больше не друзья.", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> userFriends(@PathVariable int id) {
        List<User> friends = service.findFriends(id);
        log.info("Получены друзья у пользователей {}.", id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> userFriends(
            @PathVariable int id,
            @PathVariable int friendId) {
        List<User> commonFriends = service.findCommonFriends(id, friendId);
        log.info("Получены общие друзья у пользователей {} и {}.", id, friendId);
        return commonFriends;
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> recommendations(@PathVariable("id") int id) {
        List<Film> recommend = service.recommend(id);
        log.info("Получены рекомендованные фильмы для пользователя с id {} : {}", id, recommend);
        return recommend;
    }

    @DeleteMapping("/resetDB")
    public void reset() {
        log.info("UserStorage и EventStorage очищены.");
        service.removeAll();
    }

    @GetMapping("{id}/feed")
    public List<Event> userFeed(@PathVariable int id) {
        List<Event> events = service.getEvents(id);
        log.info("Получена лента событий для пользователя с id {}", id);
        return events;
    }
}
