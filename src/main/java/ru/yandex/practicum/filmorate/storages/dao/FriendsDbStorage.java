package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storages.FriendsStorage;

import java.util.List;
import java.util.Map;

@Repository
public class FriendsDbStorage implements FriendsStorage {

    private  final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Integer> findAllById(int id){
        String sql = "SELECT USER_ID_2 FROM FRIENDS WHERE USER_ID_1 = :ID";
        return jdbcTemplate.queryForList(sql, Map.of("ID", id), Integer.class);
    }

    @Override
    public void add(int userId, int friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID_1, USER_ID_2) VALUES (:USER_ID,:FRIEND_ID)";
        jdbcTemplate.update(sql, Map.of("USER_ID",userId,"FRIEND_ID",friendId));
    }

    @Override
    public void remove(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID_1 = :USER_ID AND USER_ID_2 = :FRIEND_ID";
        jdbcTemplate.update(sql, Map.of("USER_ID",userId,"FRIEND_ID",friendId));
    }

    @Override
    public boolean isFriends(int userId, int friendId) {
        String sql = "SELECT  USER_ID_1, USER_ID_2 FROM FRIENDS WHERE USER_ID_1 = :USER_ID AND USER_ID_2 = :FRIEND_ID";
        boolean [] result = new boolean[1];
        jdbcTemplate.query(sql, Map.of("USER_ID", userId, "FRIEND_ID", friendId), rs -> {
           result[0] =  rs.last();
        });
        return result[0];
    }

    @Override
    public List<Integer> common(int userId_1, int user_id_2) {
        String sql = "SELECT F2.USER_ID_2 FROM FRIENDS F2 \n" +
                "WHERE USER_ID_1 = :USER_ID_2 AND USER_ID_2  IN  (SELECT F1.USER_ID_2 FROM FRIENDS F1 \n" +
                "WHERE USER_ID_1 = :USER_ID_1) ";
        return jdbcTemplate.queryForList(sql, Map.of("USER_ID_1", userId_1, "USER_ID_2", user_id_2), Integer.class);


    }
}
