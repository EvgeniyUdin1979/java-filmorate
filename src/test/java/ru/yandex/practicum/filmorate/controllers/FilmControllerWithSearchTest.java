package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/files/maindata.sql")
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class FilmControllerWithSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void initChanges() throws Exception {
        initDate();
    }

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
    void testSearchFilmsAnywayByupDatE() throws Exception {
        String jsonDirector = "{\n" +
                "   \"id\": 1,\n" +
                "   \"name\": \"Director updated\"\n" +
                " }";
        this.mockMvc.perform(put("/directors")
                        .header("Content-Type", "application/json; charset=utf-8")
                        .content(jsonDirector))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/films/search")
                        .param("query", "upDatE")
                        .param("by", "title", "director")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(2),
                        jsonPath("$.[1].id").value(1),
                        jsonPath("$.[1].name").value("New film #1"),
                        jsonPath("$.[1].directors.[0].name").value("Director updated"),
                        jsonPath("$.[0].id").value(2),
                        jsonPath("$.[0]name").value("Film Updated")
                );
    }

    @Test
    void testSearchFilmsAnywayEmpty() throws Exception {
        this.mockMvc.perform(get("/films/search")
                        .param("query", "не найти")
                        .param("by", "director", "title")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(0)
                );
    }

    @Test
    void testSearchFilmsByTitle() throws Exception {
        this.mockMvc.perform(get("/films/search")
                        .param("query", "UPdat")
                        .param("by", "title")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0].id").value(2),
                        jsonPath("$.[0].name").value("Film Updated"),
                        jsonPath("$.[0].directors.length()").value(0)
                );
    }

    @Test
    void testSearchFilmsByDirector() throws Exception {
        String jsonDirector = "{\n" +
                "   \"id\": 1,\n" +
                "   \"name\": \"Director updated\"\n" +
                " }";
        this.mockMvc.perform(put("/directors")
                        .header("Content-Type", "application/json; charset=utf-8")
                        .content(jsonDirector))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/films/search")
                        .param("query", "UPdat")
                        .param("by", "director")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0].id").value(1),
                        jsonPath("$.[0].name").value("New film #1"),
                        jsonPath("$.[0].directors.length()").value(1),
                        jsonPath("$.[0].directors.[0].name").value("Director updated")
                );
    }

    private void initDate() throws Exception {

        String jsonAddDirector = "{\n" +
                "     \"id\": 1,\n" +
                "     \"name\": \"New film #1\",\n" +
                "     \"releaseDate\": \"2089-04-17\",\n" +
                "     \"description\": \"New film update decription\",\n" +
                "     \"duration\": 190,\n" +
                "     \"rate\": 4,\n" +
                "     \"mpa\": { \"id\": 5},\n" +
                "     \"directors\": [{ \"id\": 1}]\n" +
                "   }";
        String jsonRenameFilm = "{\n" +
                "     \"id\": 2,\n" +
                "     \"name\": \"Film Updated\",\n" +
                "     \"releaseDate\": \"2089-04-17\",\n" +
                "     \"description\": \"New film update decription\",\n" +
                "     \"duration\": 190,\n" +
                "     \"rate\": 4,\n" +
                "     \"mpa\": { \"id\": 5}\n" +
                "   }";

        this.mockMvc.perform(put("/films")
                        .header("Content-Type", "application/json; charset=utf-8")
                        .content(jsonAddDirector))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films")
                        .header("Content-Type", "application/json; charset=utf-8")
                        .content(jsonRenameFilm))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/2/like/1")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isOk());
    }

}
