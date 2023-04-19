package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.FriendsStorage;
import ru.yandex.practicum.filmorate.storages.LikesStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage users;
    private final FriendsStorage friends;
    private final FilmStorage films;
    private final LikesStorage likes;

    public List<User> findAll() {
        return users.findAll();
    }

    public User createUser(User user) {
        if (user.getId() != 0) {
            String message = "Для добавления пользователя не нужно указывать id!";
            log.info(message + " " + user);
            throw new UserRequestException(message);
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User не имеет имени! Будет использован login {}", user);
            user.setName(user.getLogin());
        }
        return users.create(user);
    }

    public User findById(String id) {
        return findUserById(validateAndParseInt(id));
    }

    public User updateUser(User user) {
        if (user.getId() == 0) {
            String message = "Для обновления пользователя id нужно указать больше чем 0!";
            log.info(message, user);
            throw new UserRequestException(message);
        }
        findUserById(user.getId());
        return users.update(user);
    }

    public void deleteById(String userId) {
        int id = validateAndParseInt(userId);
        findUserById(id);
        users.removeById(id);
    }

    public void removeAll() {
        users.removeAll();
    }

    public List<User> findFriends(String userId) {
        int user = validateAndParseInt(userId);
        findUserById(user);
        return friends.findAllById(user)
                .stream().map(users::findById).collect(Collectors.toList());
    }

    public List<User> findCommonFriends(String userId, String forSearchId) {
        int user = validateAndParseInt(userId);
        int friend = validateAndParseInt(forSearchId);
        findUserById(user);
        findUserById(friend);
        return friends.common(user, friend);
    }

    public void addFriend(String userId, String friendId) {
        int user = validateAndParseInt(userId);
        int friend = validateAndParseInt(friendId);
        findUserById(user);
        findUserById(friend);
        friends.add(user, friend);
    }

    public void removeFriend(String userId, String friendId) {
        int user = validateAndParseInt(userId);
        int friend = validateAndParseInt(friendId);
        findUserById(user);
        findUserById(friend);
        friends.remove(user, friend);
    }

    public List<Film> recommend(String userId) {
        int id = validateAndParseInt(userId);
        findUserById(id);
        return getRecommendations(likes.allLikes(), id).stream().map(films::findById).collect(Collectors.toList());
    }

    private List<Integer> getRecommendations(Map<Integer, HashSet<Integer>> allLikes, int userId) {
        if (allLikes.get(userId) == null) {
            return new ArrayList<>();
        }
        HashMap<Integer, Integer> usersCount = new HashMap<>();
        allLikes.get(userId).forEach(filmId -> {
            for (Map.Entry<Integer, HashSet<Integer>> user : allLikes.entrySet()) {
                int anotherUserId = user.getKey();
                if (anotherUserId == userId) {
                    continue;
                }
                if (user.getValue().contains(filmId)) {
                    if (!usersCount.containsKey(anotherUserId)) {
                        usersCount.put(anotherUserId, 0);
                    }
                    int oldValue = usersCount.get(anotherUserId);
                    usersCount.put(anotherUserId, ++oldValue);
                }
            }
        });
        Optional<Map.Entry<Integer, Integer>> max = usersCount.entrySet().stream().max(Map.Entry.comparingByValue());
        if (max.isEmpty()) {
            return new ArrayList<>();
        }
        if (max.get().getValue() == 0) {
            return new ArrayList<>();
        }
        allLikes.get(max.get().getKey()).removeAll(allLikes.get(userId));
        return new ArrayList<>(allLikes.get(max.get().getKey()));
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

    private User findUserById(int id) {
        User user = users.findById(id);
        if (user == null) {
            String message = String.format("Пользователь с данным id: %d, не найден", id);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.NOT_FOUND);
        }
        return user;
    }
}
