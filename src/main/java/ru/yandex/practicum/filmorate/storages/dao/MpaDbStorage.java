package ru.yandex.practicum.filmorate.storages.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {
    NamedParameterJdbcTemplate jdbcTemplate;

    public MpaDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT ID, NAME FROM RATING ORDER BY ID;", Map.of(), new MpaMapper());
    }

    @Override
    public Mpa findById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT ID, NAME FROM RATING WHERE ID = :ID;", Map.of("ID", id), new MpaMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static class MpaMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Mpa(rs.getInt("ID"), rs.getString("NAME"));
        }
    }
}
