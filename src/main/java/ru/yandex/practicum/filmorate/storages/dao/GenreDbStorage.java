package ru.yandex.practicum.filmorate.storages.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storages.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class GenreDbStorage implements GenreStorage {

    NamedParameterJdbcTemplate jdbcTemplate;

    public GenreDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT ID, NAME FROM GENRE;", Map.of(),new GenreMapper());
    }

    @Override
    public Genre findById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT ID, NAME FROM GENRE WHERE ID = :ID;", Map.of("ID",id),new GenreMapper());
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    private class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(rs.getInt("ID"),rs.getString("NAME"));
        }
    }
}
