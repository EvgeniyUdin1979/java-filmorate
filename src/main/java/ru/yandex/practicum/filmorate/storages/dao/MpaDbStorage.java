package ru.yandex.practicum.filmorate.storages.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.MpaStorage;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private static final RowMapper<Mpa> MPA_ROW_MAPPER = (rs, rowNum) -> new Mpa(rs.getInt("ID"), rs.getString("NAME"));
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT ID, NAME FROM RATING ORDER BY ID;", Map.of(), MPA_ROW_MAPPER);
    }

    @Override
    public Mpa findById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT ID, NAME FROM RATING WHERE ID = :ID;", Map.of("ID", id), MPA_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
