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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK/*,
        classes = Application.class*/)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class DirectorsTest {
    private final MockMvc mockMvc;

    @Autowired
    DirectorsTest(WebApplicationContext wac) {

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
            this.mockMvc.perform(delete("/directors/resetDB"));
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
            case "/directors":
                List<Director> directors = mapper.readValue(string, new TypeReference<List<Director>>() {
                });
                for (Director director : directors) {
                    this.mockMvc.perform(post(urlRequest)
                            .content(mapper.writeValueAsString(director))
                            .contentType("application/json;charset=UTF-8"));
                }
        }
    }

    @Test
    void getDirectorId1() throws Exception {
        upData("src/test/resources/files/directorslist.txt", "/directors");
        this.mockMvc.perform(get("/directors/{id}", 1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("Типо режиссёр 1")
                );
    }

    @Test
    void getDirectorId99() throws Exception {
        upData("src/test/resources/files/directorslist.txt", "/directors");
        this.mockMvc.perform(get("/directors/{id}", 99))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.message").value("Режиссёра с данным id 99 не существует!")
                );
    }

    @Test
    void getAllDirectors() throws Exception {
        upData("src/test/resources/files/directorslist.txt", "/directors");
        this.mockMvc.perform(get("/directors"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(3),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("Типо режиссёр 1"),
                        jsonPath("$[1].id").value(2),
                        jsonPath("$[1].name").value("Типо режиссёр 2"),
                        jsonPath("$[2].id").value(3),
                        jsonPath("$[2].name").value("Типо режиссёр 3")

                );
    }

    @Test
    void filmID1UpdateDirectorAndGetHisFilms() throws Exception {
        upData("src/test/resources/files/directorslist.txt", "/directors");
        upData("src/test/resources/files/filmslist.txt", "/films");
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Film Updated\",\n" +
                "  \"releaseDate\": \"1989-04-17\",\n" +
                "  \"description\": \"New film update decription\",\n" +
                "  \"duration\": 190,\n" +
                "  \"rate\": 4,\n" +
                "  \"mpa\": { \"id\": 3},\n" +
                "  \"genres\": [{ \"id\": 2}],\n" +
                "  \"directors\": [{ \"id\": 1}]\n" +
                "}";

        this.mockMvc.perform(put("/films", 1)
                .content(json).contentType("application/json;charset=UTF-8"));
        this.mockMvc.perform(get("/films/director/{id}", 1))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("[0].id").value(1),
                        jsonPath("[0].name").value("Film Updated"),
                        jsonPath("[0].mpa.id").value(3),
                        jsonPath("[0].genres[0].id").value(2),
                        jsonPath("[0].directors[0].id").value(1)
                );
    }
}
