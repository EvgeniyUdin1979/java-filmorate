package ru.yandex.practicum.filmorate.storages.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storages.GenreStorage;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final RowMapper<Genre> GENRE_ROW_MAPPER = (rs, rowNum) -> new Genre(rs.getInt("ID"), rs.getString("NAME"));
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT ID, NAME FROM GENRE ORDER BY ID;", Map.of(), GENRE_ROW_MAPPER);
    }

    @Override
    public Genre findById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT ID, NAME FROM GENRE WHERE ID = :ID ;", Map.of("ID", id), GENRE_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
