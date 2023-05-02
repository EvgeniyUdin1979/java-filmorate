package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/files/maindata.sql")
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
class FilmControllerTest {
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
    void getAllFilms() throws Exception {
        this.mockMvc.perform(get("/films")
                        .contentType("application/json;charset=UTF-8")).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        MockMvcResultMatchers.jsonPath("$.length()").value(10),
                        MockMvcResultMatchers.jsonPath("$[0].id").value(1),
                        MockMvcResultMatchers.jsonPath("$[2].id").value(3)
                );
    }

    @Test
    void getByIdFilm() throws Exception {
        this.mockMvc.perform(get("/films/1")).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("New film #1"),
                        jsonPath("$.releaseDate").value("1999-04-30")
                );
    }

    @Test
    void addFailNameFilm() throws Exception {
        String json = "{\n" +
                "  \"name\": \"\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        this.mockMvc.perform(post("/films")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Название фильма не может быть отсутствовать," +
                                        " быть пустым или состоять только из пробелов!;" +
                                        " Film{id=0, name='', description='adipisicing', releaseDate=1967-03-25, duration=100};")
                );
    }

    @Test
    void addFailDescriptionFilm() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Film name\",\n" +
                "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";
        this.mockMvc.perform(post("/films")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Описание фильма не должно превышать 200 символов!;" +
                                        " Film{id=0, name='Film name', description='Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.', releaseDate=1900-03-25, duration=200};")
                );
    }

    @Test
    void addFailDateFilm() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1890-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";
        this.mockMvc.perform(post("/films")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Дата релиза не может быть раньше 28.12.1895 года!")
                );
    }

    @Test
    void addFailDurationFilm() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": -200\n" +
                "}";
        this.mockMvc.perform(post("/films")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Длительность фильма должна быть больше чем 0!;" +
                                        " Film{id=0, name='Name', description='Description', releaseDate=1900-03-25, duration=-200};")
                );
    }

    @Test
    void updateFilm() throws Exception {
        String json = "{\"id\":1,\"name\":\"test\",\"description\":\"Description\",\"releaseDate\":\"1900-03-25\",\"duration\":200,\n" +
                "    \"mpa\": { \"id\": 3},\n" +
                "    \"genres\": [{ \"id\": 1}, { \"id\": 2}, { \"id\": 3}]}\"";
        this.mockMvc.perform(put("/films")
                .content(json).header("Content-Type", "application/json"));

        this.mockMvc.perform(get("/films/1")).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("test"),
                        jsonPath("$.description").value("Description"),
                        jsonPath("$.releaseDate").value("1900-03-25")
                );
    }

    @Test
    void updateUnknownFilm() throws Exception {
        String json = "{\"id\":9999,\"name\":\"test\",\"description\":\"Description\",\"releaseDate\":\"1900-03-25\",\"duration\":200}\"));";
        this.mockMvc.perform(put("/films")
                        .content(json).header("Content-Type", "application/json")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void addLikeAndGetFilm() throws Exception {
        this.mockMvc.perform(put("/films/2/like/1")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/films/popular")
                        .param("count", "5")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(5),
                        jsonPath("$.[0].id").value(2),
                        jsonPath("$.[0]name").value("New film #2")
                );
    }

    @Test
    void removeLikeAndGetFilmWithCount5() throws Exception {
        this.mockMvc.perform(put("/films/2/like/1")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(delete("/films/2/like/1")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/films/popular")
                        .param("count", "5")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(5),
                        jsonPath("$.[0].id").value(1),
                        jsonPath("$.[0]name").value("New film #1")
                );
    }

    @Test
    void addLikeAndCheckFeed() throws Exception {
        this.mockMvc.perform(put("/films/2/like/1")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/1/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0]userId").value(1),
                        jsonPath("$.[0]eventType").value("LIKE"),
                        jsonPath("$.[0]operation").value("ADD"),
                        jsonPath("$.[0]entityId").value(2)
                );
    }

    @Test
    void removeLikeAndCheckFeed() throws Exception {
        this.mockMvc.perform(delete("/films/2/like/1")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/1/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0]userId").value(1),
                        jsonPath("$.[0]eventType").value("LIKE"),
                        jsonPath("$.[0]operation").value("REMOVE"),
                        jsonPath("$.[0]entityId").value(2)
                );
    }

    @Test
    void getPopularFilmsWithoutCount() throws Exception {
        this.mockMvc.perform(get("/films/popular")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(10)
                );
    }

    @Test
    public void deleteFilm() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/films/1")
                .header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/films/1")).andDo(print()).andExpect(status().isNotFound());
    }
}