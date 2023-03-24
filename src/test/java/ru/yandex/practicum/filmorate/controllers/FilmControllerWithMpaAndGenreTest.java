package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK/*,
        classes = Application.class*/)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class FilmControllerWithMpaAndGenreTest {
    private final MockMvc mockMvc;

    @Autowired
    FilmControllerWithMpaAndGenreTest( WebApplicationContext wac) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                })).build();
    }

    @BeforeEach
    void setUp() {
        try {
            this.mockMvc.perform(delete("/films/resetDB"));
            this.mockMvc.perform(delete("/users/resetDB"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void upData(String urlData, String urlRequest) throws Exception {
        String string = Files.readString(Path.of(urlData));
        ObjectMapper mapper = JsonMapper.builder()
                .addModules(new JavaTimeModule())
                .build();
        switch (urlRequest) {
            case "/films":
                List<Film> films = mapper.readValue(string, new TypeReference<List<Film>>() {
                });
                for (Film film : films) {
                    this.mockMvc.perform(post(urlRequest)
                            .content(mapper.writeValueAsString(film))
                            .contentType("application/json;charset=UTF-8"));
                    System.out.println(film);
                }
                break;
            case "/users":
                List<User> users = mapper.readValue(string, new TypeReference<List<User>>() {
                });
                for (User user : users) {
                    this.mockMvc.perform(post(urlRequest)
                            .content(mapper.writeValueAsString(user))
                            .contentType("application/json;charset=UTF-8"));
                }
        }
    }

    @Test
    void getMpaId1() throws Exception {
        this.mockMvc.perform(get("/mpa/{id}",1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("G")
                );
    }

    @Test
    void getMpaId99() throws Exception {
        this.mockMvc.perform(get("/mpa/{id}",99))
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
        this.mockMvc.perform(get("/genres/{id}",1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Комедия")
                );
    }

    @Test
    void getGenreId99() throws Exception {
        this.mockMvc.perform(get("/genres/{id}",99))
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
        upData("src/test/resources/files/filmslist.txt", "/films");
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

        this.mockMvc.perform(put("/films",1)
                .content(json).contentType("application/json;charset=UTF-8"));
        this.mockMvc.perform(get("/films/{id}",1))
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
        upData("src/test/resources/files/filmslist.txt", "/films");
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"New film\",\n" +
                "  \"releaseDate\": \"1999-04-30\",\n" +
                "  \"description\": \"New film about friends\",\n" +
                "  \"duration\": 120,\n" +
                "  \"mpa\": { \"id\": 3},\n" +
                "  \"genres\": [{ \"id\": 1}, { \"id\": 2}, { \"id\": 1}]\n" +
                "}";

        this.mockMvc.perform(put("/films",1)
                .content(json).contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(get("/films/{id}",1))
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
