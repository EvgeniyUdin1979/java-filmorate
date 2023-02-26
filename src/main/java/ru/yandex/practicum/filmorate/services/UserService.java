package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage users;

    @Autowired
    public UserService(UserStorage users) {
        this.users = users;
    }

    public List<User> findAll(){
        return users.findAll();
    }

    public void createUser(User user){
        if (user.getId() != 0) {
            String message ="Для добавления пользователя не нужно указывать id!";
            log.info(message + " " + user);
            throw new UserRequestException(message);
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User не имеет имени! Будет использован login {}", user);
            user.setName(user.getLogin());
        }
        users.create(user);
    }
    public User findById(String id){
        return findUserById(validateAndParseInt(id));
    }

    public void updateUser(User user){
        if (user.getId() == 0) {
            String message = "Для обновления пользователя id нужно указать больше чем 0!";
            log.info(message,user);
            throw new UserRequestException(message);
        }
        findUserById(user.getId());
        users.update(user);
    }
    public void removeAll(){
        users.removeAll();
    }





    public List<User> findFriends(String userId){
        return findUserById(validateAndParseInt(userId)).getFriendsId()
                .stream()
                .map(users::findById)
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(String userId, String forSearchId){
        User user = findUserById(validateAndParseInt(userId));
        User forSearch = findUserById(validateAndParseInt(forSearchId));
        return user.getFriendsId().stream()
                .filter(id ->forSearch.getFriendsId().contains(id))
                .map(users::findById)
                .collect(Collectors.toList());
    }

    public void addFriend(String userId, String friendId){
        User user = findUserById(validateAndParseInt(userId));
        User friend = findUserById(validateAndParseInt(friendId));
        user.getFriendsId().add(friend.getId());
        friend.getFriendsId().add(user.getId());
    }

    public void removeFriend(String userId, String friendId){
        User user = findUserById(validateAndParseInt(userId));
        User friend = findUserById(validateAndParseInt(friendId));
        user.getFriendsId().remove(friend.getId());
        friend.getFriendsId().remove(user.getId());
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
