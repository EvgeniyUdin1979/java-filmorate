package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository("userDAO")
public class UserDbStorage implements UserStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public UserDbStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT ID,EMAIL,LOGIN,NAME,BIRTHDAY FROM USERS";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public User findById(int id) {
        try {
            String sql ="SELECT ID,EMAIL,LOGIN,NAME,BIRTHDAY FROM USERS WHERE ID = :ID;";

            return jdbcTemplate.queryForObject(sql,Map.of("ID",id), new UserMapper());
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public User create(User user) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("EMAIL",user.getEmail())
                .addValue("LOGIN",user.getLogin())
                .addValue("NAME",user.getName())
                .addValue("BIRTHDAY",user.getBirthday());
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("USERS")
                .usingGeneratedKeyColumns("ID");
        int id = insert.executeAndReturnKey(param).intValue();
        return findById(id);
    }

    @Override
    public void removeById(int id) {
        String sql = "DELETE FROM USERS WHERE ID = :ID";
        jdbcTemplate.update(sql,Map.of("ID",id));
    }

    @Override
    public User update(User user) {
        String sql = "MERGE INTO USERS (ID,EMAIL,LOGIN,NAME,BIRTHDAY) " +
                "VALUES (:ID,:EMAIL,:LOGIN,:NAME,:BIRTHDAY)";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("ID",user.getId())
                .addValue("EMAIL",user.getEmail())
                .addValue("LOGIN",user.getLogin())
                .addValue("NAME",user.getName())
                .addValue("BIRTHDAY",user.getBirthday());
        jdbcTemplate.update(sql,param);

        return findById(user.getId());
    }

    @Override
    public void removeAll() {
        jdbcTemplate.update("DELETE FROM USERS; ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;",Map.of());
    }


    private static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getInt("ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate()
            );
        }

    }
}
