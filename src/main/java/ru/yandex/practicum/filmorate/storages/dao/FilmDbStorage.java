package ru.yandex.practicum.filmorate.storages.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
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
import java.util.*;

@Repository("filmDAO")
public class FilmDbStorage implements FilmStorage {
    private static final RowMapper<Film> FILM_ROW_MAPPER = (rs, rowNum) -> new Film(rs.getInt("FILM_ID"),
            rs.getString("FILM_NAME"),
            rs.getString("FILM_DESCRIPTION"),
            rs.getDate("FILM_RELEASE_DATE").toLocalDate(),
            rs.getInt("FILM_DURATION"),
            rs.getInt("FILM_LIKE_QUANTITY"),
            new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME")));

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public FilmDbStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT F.ID AS FILM_ID,\n" +
                "F.NAME AS FILM_NAME,\n" +
                "F.DESCRIPTION AS FILM_DESCRIPTION, \n" +
                "F.RELEASE_DATE AS FILM_RELEASE_DATE, \n" +
                "F.DURATION AS FILM_DURATION, \n" +
                "F.LIKE_QUANTITY AS FILM_LIKE_QUANTITY,\n" +
                "R.ID AS RATING_ID,\n" +
                "R.NAME AS RATING_NAME \n" +
                "FROM FILM F\n" +
                "JOIN RATING R ON R.ID = F.RATING_ID;";
        List<Film> films = jdbcTemplate.query(sql, FILM_ROW_MAPPER);
        String sqlGenres = "SELECT FG.FILM_ID ,FG.GENRE_ID,G.NAME FROM FILM_X_GENRE FG JOIN GENRE G ON G.ID = FG.GENRE_ID\n" +
                "WHERE FILM_ID IN (SELECT ID FROM FILM);";
        HashMap<Integer, Set<Genre>> map = new HashMap<>();
        jdbcTemplate.query(sqlGenres, rs -> {
            Genre genre = new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME"));
            int key = rs.getInt("FILM_ID");
            if (map.containsKey(key)) {
                map.get(key).add(genre);
            } else {
                Set<Genre> genres = new HashSet<>();
                genres.add(genre);
                map.put(key, genres);
            }
        });
        for (Film film : films) {
            if (map.containsKey(film.getId())) {
                film.getGenres().addAll(map.get(film.getId()));
            }
        }
        return films;
    }

    @Override
    public Film findById(int id) {
        try {
            String sql = "SELECT " +
                    "F.ID AS FILM_ID," +
                    "F.NAME AS FILM_NAME," +
                    "F.DESCRIPTION AS FILM_DESCRIPTION, " +
                    "F.RELEASE_DATE AS FILM_RELEASE_DATE, " +
                    "F.DURATION AS FILM_DURATION, " +
                    "F.LIKE_QUANTITY AS FILM_LIKE_QUANTITY, " +
                    "R.ID AS RATING_ID," +
                    "R.NAME AS RATING_NAME " +
                    "FROM FILM F " +
                    "JOIN RATING R ON R.ID = F.RATING_ID " +
                    "WHERE F.ID = :ID;";
            Film film = jdbcTemplate.queryForObject(sql, Map.of("ID", id), FILM_ROW_MAPPER);
            String sqlGenres = "SELECT FG.FILM_ID ,FG.GENRE_ID,G.NAME FROM FILM_X_GENRE FG JOIN GENRE G ON G.ID = FG.GENRE_ID " +
                    "WHERE FILM_ID =:ID;";
            jdbcTemplate.query(sqlGenres, Map.of("ID", id), new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    film.getGenres()
                            .add(new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME")));
                }
            });
            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film create(Film film) {
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
        addGenres(id, new ArrayList<>(film.getGenres()));
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
        jdbcTemplate.update("DELETE FROM FILM_X_GENRE WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_X_GENRE WHERE FILM_ID = :ID);", Map.of("ID", film.getId()));
        addGenres(film.getId(), new ArrayList<>(film.getGenres()));
        return findById(film.getId());
    }

    private void addGenres(int id, List<Genre> genres) {
        if (genres.size() > 0) {
            Map<String, Integer>[] maps = new Map[genres.size()];
            for (int i = 0; i < genres.size(); i++) {
                maps[i] = Map.of("ID", genres.get(i).getId());
            }
            jdbcTemplate.batchUpdate(String.format("INSERT INTO FILM_X_GENRE (FILM_ID,GENRE_ID) VALUES (%d,:ID)", id), maps);
        }
    }

    @Override
    public void removeAll() {
        jdbcTemplate.update("DELETE FROM FILM; ALTER TABLE FILM ALTER COLUMN ID RESTART WITH 1", Map.of());
    }

    @Override
    public boolean exists(int id) {
        String query = "SELECT EXISTS(SELECT * FROM film WHERE id = :ID)";
        return jdbcTemplate.queryForObject(query, Map.of("ID",id), Boolean.class);
    }

    static class FilmMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(rs.getInt("FILM_ID"),
                    rs.getString("FILM_NAME"),
                    rs.getString("FILM_DESCRIPTION"),
                    rs.getDate("FILM_RELEASE_DATE").toLocalDate(),
                    rs.getInt("FILM_DURATION"),
                    rs.getInt("FILM_LIKE_QUANTITY"),
                    new Mpa(rs.getInt("RATING_ID"), rs.getString("RATING_NAME")));
        }
    }
}
