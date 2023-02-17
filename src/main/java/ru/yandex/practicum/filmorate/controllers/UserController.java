package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controllers.repositories.UsersRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UsersRepository users;

    @GetMapping
    public List<User> getAllUsers(){
        log.info("Получены данные всех пользователей.");
        return users.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getFilmById(@PathVariable("id") int id){
        User user = users.getUser(id);
        log.info(String.format("Получены данные по пользователю id: %d.",id));
        return user;
    }

    @PostMapping
    public User addUser(@RequestBody User user){
        users.addUser(user);
        log.info("Пользоваель добавлен.");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user ){
        users.updateUser(user);
        log.info("Данные пользователя обновленны.");
        return user;
    }
}
