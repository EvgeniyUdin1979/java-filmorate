package ru.yandex.practicum.filmorate.storages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storages.dao.DirectorStorage;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository("directorDAO")
public class DirectorDbStorage implements DirectorStorage {
    private static final RowMapper<Director> DIRECTOR_ROW_MAPPER = (rs, rowNum) ->
            new Director(rs.getInt("ID"), rs.getString("NAME"));
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public DirectorDbStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query("SELECT ID, NAME FROM DIRECTOR ORDER BY ID;", Map.of(), DIRECTOR_ROW_MAPPER);
    }

    @Override
    public Director findById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT ID, NAME FROM DIRECTOR WHERE ID = :ID;",
                    Map.of("ID", id), DIRECTOR_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Director create(Director director) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("NAME", director.getName());
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("DIRECTOR")
                .usingGeneratedKeyColumns("ID");
        int id = insert.executeAndReturnKey(param).intValue();
        return findById(id);
    }

    @Override
    public Director update(Director director) {
        String sql = "MERGE INTO DIRECTOR (ID,NAME) " +
                "VALUES (:ID,:NAME)";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("ID", director.getId())
                .addValue("NAME", director.getName());
        jdbcTemplate.update(sql, param);

        return findById(director.getId());
    }

    @Override
    public void removeById(int id) {
        String sql = "DELETE FROM DIRECTOR WHERE ID = :ID";
        jdbcTemplate.update(sql, Map.of("ID", id));
    }

    @Override
    public boolean isExists(int id) {
        String sql = "SELECT EXISTS(SELECT * FROM DIRECTOR WHERE ID = :ID);";
        Boolean result = jdbcTemplate.queryForObject(sql, Map.of("ID", id), Boolean.class);
        return result;
    }

    @Override
    public void removeAll() {
        jdbcTemplate.update("DELETE FROM DIRECTOR; ALTER TABLE DIRECTOR ALTER COLUMN ID RESTART WITH 1;", Map.of());
    }
}
