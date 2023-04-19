package ru.yandex.practicum.filmorate.storages.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.FriendsStorage;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Integer> findAllById(int id) {
        String sql = "SELECT USER_ID_2 FROM FRIENDS WHERE USER_ID_1 = :ID";
        return jdbcTemplate.queryForList(sql, Map.of("ID", id), Integer.class);
    }

    @Override
    public void add(int userId, int friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID_1, USER_ID_2) VALUES (:USER_ID,:FRIEND_ID)";
        jdbcTemplate.update(sql, Map.of("USER_ID", userId, "FRIEND_ID", friendId));
    }

    @Override
    public void remove(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID_1 = :USER_ID AND USER_ID_2 = :FRIEND_ID";
        jdbcTemplate.update(sql, Map.of("USER_ID", userId, "FRIEND_ID", friendId));
    }

    @Override
    public boolean isFriends(int userId, int friendId) {
        String sql = "SELECT  USER_ID_1, USER_ID_2 FROM FRIENDS WHERE USER_ID_1 = :USER_ID AND USER_ID_2 = :FRIEND_ID";
        boolean[] result = new boolean[1];
        jdbcTemplate.query(sql, Map.of("USER_ID", userId, "FRIEND_ID", friendId), rs -> {
            result[0] = rs.last();
        });
        return result[0];
    }

    @Override
    public List<User> common(int userId1, int userId2) {
        String sql = "select * from USERS u, FRIENDS f, FRIENDS o \n" +
                "       where u.ID = f.USER_ID_2 AND u.ID = o.USER_ID_2 AND f.USER_ID_1= 1 AND o.USER_ID_1 = 2";
        return jdbcTemplate.query(sql, Map.of("USER_ID_1", userId1, "USER_ID_2", userId2), UserDbStorage.USER_ROW_MAPPER);


    }
}
