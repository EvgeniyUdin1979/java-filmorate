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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class FilmControllerWithSearchTest {

    @Autowired
    MockMvc mockMvc;


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
        }
    }

    @Test
    public void testSearchFilmsAnywayByupDatE() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");
        initDate();

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
    public void testSearchFilmsAnywayEmpty() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");
        initDate();

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
    public void testSearchFilmsByTitle() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");
        initDate();

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
    public void testSearchFilmsByDirector() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");
        initDate();

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
        String jsonDirector = "{\n" +
                "   \"id\": 1,\n" +
                "   \"name\": \"Director updated\"\n" +
                " }";
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
        this.mockMvc.perform(post("/directors")
                        .header("Content-Type", "application/json; charset=utf-8")
                        .content(jsonDirector))
                .andExpect(status().isOk());
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
