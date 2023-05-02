package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.errors.UserRequestException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storages.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storages.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.storages.dao.LikesStorage;
import ru.yandex.practicum.filmorate.storages.dao.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friends;
    private final EventService eventService;
    private final FilmStorage films;
    private final LikesStorage likesStorage;



    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        if (user.getId() != 0) {
            String message = String.format("Для добавления пользователя не нужно указывать id! %s", user);
            log.info(message);
            throw new UserRequestException(message);
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User не имеет имени! Будет использован login {}", user);
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User findById(int id) {
        findUserById(id);
        return userStorage.findById(id);
    }

    public User updateUser(User user) {
        if (user.getId() == 0) {
            String message = "Для обновления пользователя id нужно указать больше чем 0!";
            log.info(message);
            throw new UserRequestException(message);
        }
        findUserById(user.getId());
        return userStorage.update(user);
    }

    public void deleteById(int id) {
        findUserById(id);
        userStorage.removeById(id);
    }

    public void removeAll() {
        userStorage.removeAll();
        eventService.removeAll();
    }

    public List<User> findFriends(int id) {
        findUserById(id);
        return friends.findAllById(id)
                .stream().map(userStorage::findById).collect(Collectors.toList());
    }

    public List<User> findCommonFriends(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        return friends.common(userId, friendId);
    }

    public void addFriend(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        friends.add(userId, friendId);
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(friendId)
                .build());
    }

    public void removeFriend(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        friends.remove(userId, friendId);
        eventService.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .entityId(friendId)
                .build());
    }

    public List<Event> getEvents(int id) {
        findUserById(id);
        return eventService.findByUserId(id);
    }

    public List<Film> recommend(int id) {
        findUserById(id);
        return getRecommendations(likesStorage.allLikes(), id).stream().map(films::findById).collect(Collectors.toList());
    }

    public void findUserById(int id) {
        if (!userStorage.isExists(id)) {
            String message = String.format("Пользователь с данным id: %d, не найден", id);
            log.info(message);
            throw new UserRequestException(message, HttpStatus.NOT_FOUND);
        }
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

}
