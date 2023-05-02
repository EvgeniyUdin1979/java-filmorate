package ru.yandex.practicum.filmorate.storages.dao;

import java.util.HashSet;
import java.util.Map;

public interface LikesStorage {

    void add(int userId, int filmId);

    void remove(int userId, int filmId);

    Map<Integer, HashSet<Integer>> allLikes();
}
