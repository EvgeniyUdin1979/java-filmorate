package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storages.LikesStorage;

import java.util.Map;

@Repository
public class LikesDbStorage implements LikesStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LikesDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(int userId, int filmId) {
        String sql = "INSERT INTO LIKES (FILM_ID,USER_ID) VALUES (:FILM_ID,:USER_ID) ON CONFLICT DO NOTHING;\n" +
                "UPDATE FILM SET LIKE_QUANTITY  = \n" +
                "(SELECT COUNT (USER_ID) AS QUANTITY FROM LIKES WHERE FILM_ID = :FILM_ID GROUP BY FILM_ID)\n" +
                "WHERE ID = :FILM_ID;";
        jdbcTemplate.update(sql, Map.of("FILM_ID", filmId, "USER_ID", userId));
    }

    @Override
    public void remove(int userId, int filmId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = :FILM_ID AND USER_ID = :USER_ID;\n" +
                "UPDATE FILM SET LIKE_QUANTITY  = \n" +
                "(SELECT COUNT (USER_ID) AS QUANTITY FROM LIKES WHERE FILM_ID = :FILM_ID GROUP BY FILM_ID)\n" +
                "WHERE ID = :FILM_ID;";
        jdbcTemplate.update(sql, Map.of("FILM_ID", filmId, "USER_ID", userId));
    }
}