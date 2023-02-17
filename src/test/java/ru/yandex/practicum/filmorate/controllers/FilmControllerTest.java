package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
     void setUp() {
        String json = "{\n" +
                "  \"name\": \"nisi eiusmod\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .content(json).header("Content-Type", "application/json");
        try {
            this.mockMvc.perform(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllFilms() throws Exception {
        this.mockMvc.perform(get("/films")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"id\":1,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100},{\"id\":2,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100},{\"id\":3,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}]")));
    }

    @Test
    void addFilm() throws Exception{
            this.mockMvc.perform(get("/films/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"id\":1,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}"));
    }
    @Test
    void addFailNameFilm() throws Exception{
        String json = "{\n" +
                "  \"name\": \"\",\n" +
                "  \"description\": \"adipisicing\",\n" +
                "  \"releaseDate\": \"1967-03-25\",\n" +
                "  \"duration\": 100\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .content(json)
                .header("Content-Type", "application/json; charset=utf-8");
            this.mockMvc.perform(builder)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"message\":\"Название фильма не может быть пустым или отсутствовать!\"}"));
    }

    @Test
    void addFailDescriptionFilm() throws Exception{
        String json = "{\n" +
                "  \"name\": \"Film name\",\n" +
                "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .content(json)
                .header("Content-Type", "application/json; charset=utf-8");
        this.mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Описание фильма не должно превышать 200 символов!\"}"));
    }

    @Test
    void addFailDateFilm() throws Exception{
        String json = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1890-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .content(json)
                .header("Content-Type", "application/json; charset=utf-8");
        this.mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Дата релиза не может быть раньше 28.12.1895 года!\"}"));
    }

    @Test
    void addFailDurationFilm() throws Exception{
        String json = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": -200\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .content(json)
                .header("Content-Type", "application/json; charset=utf-8");
        this.mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Длительность фильма должна быть больше чем 0!\"}"));
    }

    @Test
    void updateFilm() throws Exception {
        String json = "{\"id\":1,\"name\":\"test\",\"description\":\"Description\",\"releaseDate\":\"1900-03-25\",\"duration\":200}\"));";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/films")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder);
        this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"test\",\"description\":\"Description\",\"releaseDate\":\"1900-03-25\",\"duration\":200}"));
    }
    @Test
    void updateUnknownFilm() throws Exception {
        String json = "{\"id\":9999,\"name\":\"test\",\"description\":\"Description\",\"releaseDate\":\"1900-03-25\",\"duration\":200}\"));";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/films")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }
}