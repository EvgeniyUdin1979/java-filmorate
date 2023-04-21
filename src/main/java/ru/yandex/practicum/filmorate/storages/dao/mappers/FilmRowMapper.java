package ru.yandex.practicum.filmorate.storages.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .likesQuantity(rs.getInt("film_like_quantity"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")))
                .build();
        film.getLikesId().addAll(parseLikes(rs));
        film.getGenres().addAll(parseGenres(rs));
        film.getDirectors().addAll(parseDirectors(rs));
        return film;
    }

    private Set<Integer> parseLikes(ResultSet rs) throws SQLException {
        String str = rs.getString("likes");
        return  str == null || str.matches("[,@]+") ?
                new HashSet<>()
                : Arrays.stream(rs.getString("likes").split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    private Set<Director> parseDirectors(ResultSet rs) throws SQLException {
        String str = rs.getString("directors");
        return  str == null || str.matches("[,@]+") ?
                new HashSet<>()
                : Arrays.stream(rs.getString("directors").split(","))
                .map(director -> {
                    String[] data = director.split("@");
                    int directorId = Integer.parseInt(data[0]);
                    return new Director(directorId, data[1]);
                })
                .collect(Collectors.toSet());
    }

    private Set<Genre> parseGenres(ResultSet rs) throws SQLException {
        String str = rs.getString("genres");
        return  str == null || str.matches("[,@]+") ?
                new HashSet<>()
                : Arrays.stream(rs.getString("genres").split(","))
                .map(genre -> {
                    String[] data = genre.split("@");
                    int genreId = Integer.parseInt(data[0]);
                    return new Genre(genreId, data[1]);
                }).collect(Collectors.toSet());
    }
}