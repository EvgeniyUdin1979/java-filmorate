package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/files/maindata.sql")
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class FilmControllerWithMpaAndGenreTest {

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void setUp() {
        try {
            this.mockMvc.perform(delete("/films/resetDB"));
            this.mockMvc.perform(delete("/users/resetDB"));
            this.mockMvc.perform(delete("/directors/resetDB"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getMpaId1() throws Exception {
        this.mockMvc.perform(get("/mpa/{id}", 1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("G")
                );
    }

    @Test
    void getMpaId99() throws Exception {
        this.mockMvc.perform(get("/mpa/{id}", 99))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.message").value("Рейтинга с данным id 99  не существует!")
                );
    }

    @Test
    void getAllMpa() throws Exception {
        this.mockMvc.perform(get("/mpa"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(5),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("G"),
                        jsonPath("$[4].id").value(5),
                        jsonPath("$[4].name").value("NC-17")

                );
    }

    @Test
    void getGenreId1() throws Exception {
        this.mockMvc.perform(get("/genres/{id}", 1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Комедия")
                );
    }

    @Test
    void getGenreId99() throws Exception {
        this.mockMvc.perform(get("/genres/{id}", 99))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.message").value("Жанра с id 99не существует!")
                );
    }

    @Test
    void getAllGenre() throws Exception {
        this.mockMvc.perform(get("/genres"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(6),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("Комедия"),
                        jsonPath("$[5].id").value(6),
                        jsonPath("$[5].name").value("Боевик")

                );
    }

    @Test
    void filmID1UpdateGenre() throws Exception {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Film Updated\",\n" +
                "  \"releaseDate\": \"1989-04-17\",\n" +
                "  \"description\": \"New film update decription\",\n" +
                "  \"duration\": 190,\n" +
                "  \"rate\": 4,\n" +
                "  \"mpa\": { \"id\": 5},\n" +
                "  \"genres\": [{ \"id\": 2}]\n" +
                "}";

        this.mockMvc.perform(put("/films", 1)
                .content(json).contentType("application/json;charset=UTF-8"));
        this.mockMvc.perform(get("/films/{id}", 1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Film Updated"),
                        jsonPath("$.mpa.id").value(5),
                        jsonPath("$.genres[0].id").value(2)
                );
    }

    @Test
    void filmID1UpdateGenreWithDuplicate() throws Exception {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"New film\",\n" +
                "  \"releaseDate\": \"1999-04-30\",\n" +
                "  \"description\": \"New film about friends\",\n" +
                "  \"duration\": 120,\n" +
                "  \"mpa\": { \"id\": 3},\n" +
                "  \"genres\": [{ \"id\": 1}, { \"id\": 2}, { \"id\": 1}]\n" +
                "}";

        this.mockMvc.perform(put("/films", 1)
                .content(json).contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(get("/films/{id}", 1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("New film"),
                        jsonPath("$.mpa.id").value(3),
                        jsonPath("$.genres[0].id").value(1),
                        jsonPath("$.genres[1].id").value(2)
                );
    }


}
