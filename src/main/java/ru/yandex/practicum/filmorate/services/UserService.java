package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage users;

    @Autowired
    public UserService(UserStorage users) {
        this.users = users;
    }

    public List<User> findFriends(String userId){
        return users.findById(userId).getFriendsId()
                .stream()
                .map(integer -> users.findById(integer.toString()))
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(String userId, String forSearchId){
        User user = users.findById(userId);
        User forSearch = users.findById(forSearchId);
        return user.getFriendsId().stream()
                .filter(id ->forSearch.getFriendsId().contains(id))
                .map(id ->users.findById(id.toString()))
                .collect(Collectors.toList());
    }
    public void add(String userId, String friendId){
        User user = users.findById(userId);
        User friend = users.findById(friendId);
        user.getFriendsId().add(friend.getId());
    }

    public void remove(String userId, String friendId){
        User user = users.findById(userId);
        User friend = users.findById(friendId);
        user.getFriendsId().remove(friend.getId());
    }




}
