package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("filmDAO")
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public FilmDbStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT ID,NAME,DESCRIPTION,RELEASE_DATE,DURATION,LIKE_QUANTITY,RATING_ID  FROM FILM;";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());
        return films;
    }

    @Override
    public Film findById(int id) {
        try {
            String sql = "SELECT ID,NAME,DESCRIPTION,RELEASE_DATE,DURATION,LIKE_QUANTITY,RATING_ID  FROM FILM WHERE ID = :ID;";
            return jdbcTemplate.queryForObject(sql, Map.of("ID", id), new FilmMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO FILM (NAME,DESCRIPTION,RELEASE_DATE,DURATION,LIKE_QUANTITY,RATING_ID)" +
                " VALUES (:NAME,:DESCRIPTION,:RELEASE_DATE,:DURATION,:LIKE_QUANTITY,:RATING_ID)";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("NAME", film.getName())
                .addValue("DESCRIPTION", film.getDescription())
                .addValue("RELEASE_DATE", film.getReleaseDate())
                .addValue("DURATION", film.getDuration())
                .addValue("LIKE_QUANTITY", film.getLikesQuantity())
                .addValue("RATING_ID", film.getMpa().getId());
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("FILM")
                .usingGeneratedKeyColumns("ID");
        int id = insert.executeAndReturnKey(param).intValue();
        addGenres(id,new ArrayList<>(film.getGenres()));
        return findById(id);
    }

    @Override
    public void removeById(int id) {
        String sql = "DELETE FROM FILM WHERE ID = :ID";
        jdbcTemplate.update(sql, Map.of("ID", id));
    }

    @Override
    public Film update(Film film) {
        String sql = "MERGE INTO FILM (ID,NAME,DESCRIPTION,RELEASE_DATE,DURATION,LIKE_QUANTITY,RATING_ID)" +
                " VALUES (:ID,:NAME,:DESCRIPTION,:RELEASE_DATE,:DURATION,:LIKE_QUANTITY,:RATING_ID)";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("ID", film.getId())
                .addValue("NAME", film.getName())
                .addValue("DESCRIPTION", film.getDescription())
                .addValue("RELEASE_DATE", film.getReleaseDate())
                .addValue("DURATION", film.getDuration())
                .addValue("LIKE_QUANTITY", film.getLikesQuantity())
                .addValue("RATING_ID", film.getMpa().getId());
        jdbcTemplate.update(sql, param);
        jdbcTemplate.update("DELETE FROM FILM_X_GENRE WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_X_GENRE WHERE FILM_ID = :ID);", Map.of("ID",film.getId()));
        addGenres(film.getId(),new ArrayList<>(film.getGenres()));
        return findById(film.getId());
    }

    private void addGenres(int id,List<Genre> genres) {
        if (genres.size() > 0){
            StringBuilder sqlGenres = new StringBuilder("INSERT INTO FILM_X_GENRE (FILM_ID,GENRE_ID) VALUES ");
            for (Genre genre : genres) {
                sqlGenres.append("(:ID,").append(genre.getId()).append("),");
            }
            sqlGenres.replace(sqlGenres.length() - 1, sqlGenres.length(), ";");
            jdbcTemplate.update(sqlGenres.toString(), Map.of("ID", id));
        }
    }



    @Override
    public void removeAll() {
        jdbcTemplate.update("DELETE FROM FILM; ALTER TABLE FILM ALTER COLUMN ID RESTART WITH 1",Map.of());
    }

    private class FilmMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film(rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getDate("RELEASE_DATE").toLocalDate(),
                    rs.getInt("DURATION"),
                    rs.getInt("LIKE_QUANTITY"),
                    getMpa(rs.getInt("RATING_ID")));
            film.getLikesId().addAll(jdbcTemplate.queryForList("SELECT USER_ID FROM LIKES WHERE FILM_ID =:ID",
                    Map.of("ID", film.getId()),
                    Integer.class));
            film.getGenres().addAll(jdbcTemplate
                    .queryForList("SELECT FG.GENRE_ID AS id, G.NAME AS name FROM FILM_X_GENRE FG JOIN GENRE G ON G.ID = FG.GENRE_ID WHERE FG.FILM_ID = :ID ",
                            Map.of("ID", film.getId())).stream().map(som -> new Genre((int) som.get("id"), (String) som.get("name"))).collect(Collectors.toList()));
            return film;
        }

        private Mpa getMpa(int id) {
            String sql = "SELECT * FROM RATING WHERE ID =:ID";
            return jdbcTemplate.queryForObject(sql, Map.of("ID", id), (rs, rowNum) -> new Mpa(rs.getInt("ID"), rs.getString("NAME")));
        }

    }

}
