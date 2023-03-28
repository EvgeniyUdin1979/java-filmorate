package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.FriendsStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage users;
    private final FriendsStorage friends;

    @Autowired
    public UserService(@Qualifier("userDAO")UserStorage users, FriendsStorage friends) {
        this.users = users;
        this.friends = friends;
    }

    public List<User> findAll(){
        return users.findAll();
    }

    public User createUser(User user){
        if (user.getId() != 0) {
            String message ="Для добавления пользователя не нужно указывать id!";
            log.info(message + " " + user);
            throw new UserRequestException(message);
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User не имеет имени! Будет использован login {}", user);
            user.setName(user.getLogin());
        }
       return users.create(user);
    }
    public User findById(String id){
        return findUserById(validateAndParseInt(id));
    }

    public User updateUser(User user){
        if (user.getId() == 0) {
            String message = "Для обновления пользователя id нужно указать больше чем 0!";
            log.info(message,user);
            throw new UserRequestException(message);
        }
        findUserById(user.getId());
       return users.update(user);
    }

    public void removeAll(){
        users.removeAll();
    }

    public List<User> findFriends(String userId){
        int user = validateAndParseInt(userId);
        findUserById(user);
        return friends.findAllById(user)
                .stream().map(users::findById).collect(Collectors.toList());
    }

    public List<User> findCommonFriends(String userId, String forSearchId){
        int user = validateAndParseInt(userId);
        int friend = validateAndParseInt(forSearchId);
        findUserById(user);
        findUserById(friend);
        return friends.common(user,friend)
                .stream().map(users::findById).collect(Collectors.toList());
    }

    public void addFriend(String userId, String friendId){
        int user = validateAndParseInt(userId);
        int friend = validateAndParseInt(friendId);
        findUserById(user);
        findUserById(friend);
        friends.add(user,friend);
    }

    public void removeFriend(String userId, String friendId){
        int user = validateAndParseInt(userId);
        int friend = validateAndParseInt(friendId);
        findUserById(user);
        findUserById(friend);
        friends.remove(user,friend);
    }

    private int validateAndParseInt(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            String message = String.format("Данный id: %s, не целое число!", id);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.BAD_REQUEST);
        }
    }

    private User findUserById(int id){
        User user = users.findById(id);
        if (user == null){
            String message = String.format("Пользователь с данным id: %d, не найден", id);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.NOT_FOUND);
        }
        return user;
    }




}
