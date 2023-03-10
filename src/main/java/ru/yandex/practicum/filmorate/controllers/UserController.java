package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public UserController( UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получены данные всех пользователей.");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") String id) {
        User user = service.findById(id);
        log.info(String.format("Получены данные по пользователю id: %s.", id));
        return user;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        service.createUser(user);
        log.info("Пользоваель добавлен: " + user);
        return user;
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable String id,
            @PathVariable String friendId) {
        service.addFriend(id, friendId);
        log.info("Пользователь {} и пользователь {} теперь друзья.", id, friendId);

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        service.updateUser(user);
        log.info("Данные пользователя обновленны.");
        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable String id,
                             @PathVariable String friendId) {
        service.removeFriend(id, friendId);
        log.info("Пользователь {} и пользователь {} больше не друзья.", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> userFriends(@PathVariable String id) {
        List<User> friends = service.findFriends(id);
        log.info("Получены друзья у пользователей {}.", id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> userFriends(
                                  @PathVariable String id,
                                  @PathVariable String friendId) {
        List<User> commonFriends = service.findCommonFriends(id, friendId);
        log.info("Получены общие друзья у пользователей {} и {}.", id, friendId);
        return commonFriends;
    }

    @DeleteMapping("/resetDB")
    public void reset() {
        log.info("UserStorage очищена.");
        service.removeAll();
    }
}
