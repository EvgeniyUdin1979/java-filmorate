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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
class UserControllerTest {


    private final MockMvc mockMvc;

    @Autowired
    UserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
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
                            .contentType(MediaType.APPLICATION_JSON));
                }
                break;
            case "/users":
                List<User> users = mapper.readValue(string, new TypeReference<List<User>>() {
                });
                for (User user : users) {
                    this.mockMvc.perform(post(urlRequest)
                            .content(mapper.writeValueAsString(user))
                            .contentType(MediaType.APPLICATION_JSON));
                }
        }
    }

    @Test
    void getAllUsers() throws Exception {
        upData("src/test/resources/files/userslist.txt", "/users");
        this.mockMvc.perform(get("/users"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(10),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[9].id").value(10)
                );
    }

    @Test
    void updateUser() throws Exception {
        upData("src/test/resources/files/userslist.txt", "/users");
        String json = "{\n" +
                "  \"login\": \"doloreUpdate\",\n" +
                "  \"name\": \"est adipisicing\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"mail@yandex.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        this.mockMvc.perform(put("/users")
                .content(json)
                .header("Content-Type", "application/json"));

        this.mockMvc.perform(get("/users/1")).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.email").value("mail@yandex.ru"),
                        jsonPath("$.login").value("doloreUpdate"),
                        jsonPath("$.name").value("est adipisicing")
                );
    }

    @Test
    void addFailLoginUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"\",\n" +
                "  \"name\": \"est adipisicing\",\n" +
                "  \"email\": \"mail@yandex.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        this.mockMvc.perform(post("/users")
                        .content(json)
                        .header("Content-Type", "application/json")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Логин может состоять только из латинских букв и подчеркивания." +
                                        " Логин не может быть пустым, отсутствовать или состоять только из пробелов!;" +
                                        " User{id=0, email='mail@yandex.ru', login='', name='est adipisicing', birthday=1976-09-20};")
                );
    }

    @Test
    void addFailEmailUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"doloreUpdate\",\n" +
                "  \"name\": \"est adipisicing\",\n" +
                "  \"email\": \"mail_yandex.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        this.mockMvc.perform(post("/users")
                        .content(json)
                        .header("Content-Type", "application/json")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Не корректный Email!;" +
                                        " User{id=0, email='mail_yandex.ru', login='doloreUpdate', name='est adipisicing', birthday=1976-09-20};")
                );
    }

    @Test
    void addFailBirthDayEmailUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"2446-08-20\"\n" +
                "}";
        this.mockMvc.perform(post("/users")
                        .content(json)
                        .header("Content-Type", "application/json")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Дата рождения должна быть в прошлом!;" +
                                        " User{id=0, email='test@mail.ru', login='dolore', name='', birthday=2446-08-20};")
                );
    }

    @Test
    void addEmptyBirthDayEmailUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"\"\n" +
                "}";
        this.mockMvc.perform(post("/users")
                        .content(json).header("Content-Type", "application/json")).andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message")
                                .value("Дата рождения не может отсутствовать!;" +
                                        " User{id=0, email='test@mail.ru', login='dolore', name='', birthday=null};")
                );
    }

    @Test
    void addWithoutNameUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        this.mockMvc.perform(post("/users")
                        .content(json).header("Content-Type", "application/json")).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.login").value("dolore"),
                        jsonPath("$.name").value("dolore")
                );
    }

    @Test
    void updateFailUser() throws Exception {
        upData("src/test/resources/files/userslist.txt", "/users");
        String json = "{\n" +
                "  \"login\": \"doloreUpdate\",\n" +
                "  \"name\": \"est adipisicing\",\n" +
                "  \"id\": 9999,\n" +
                "  \"email\": \"mail@yandex.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/users")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"message\":\"Пользователь с данным id: 9999, не найден\"}"));
    }

    @Test
    public void deleteUser() throws Exception {
        upData("src/test/resources/files/userslist.txt", "/users");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/users/1")
                .header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/users/1")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void addFriendAndCheckFeed() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");
        this.mockMvc.perform(put("/users/1/friends/2")
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
                        jsonPath("$.[0]eventType").value("FRIEND"),
                        jsonPath("$.[0]operation").value("ADD"),
                        jsonPath("$.[0]entityId").value(2)
                );
    }

    @Test
    void removeFriendAndCheckFeed() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");
        this.mockMvc.perform(delete("/users/1/friends/2")
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
                        jsonPath("$.[0]eventType").value("FRIEND"),
                        jsonPath("$.[0]operation").value("REMOVE"),
                        jsonPath("$.[0]entityId").value(2)
                );
    }

    @Test
    void checkFeedForMissingUser() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");

        this.mockMvc.perform(get("/users/9999/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().string("{\"message\":\"Пользователь с данным id: 9999, не найден\"}"));
    }

    @Test
    void checkFeedForNegativeUserId() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");

        this.mockMvc.perform(get("/users/-1/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().string("{\"message\":\"Пользователь с данным id: -1, не найден\"}"));
    }

    @Test
    void checkFeedForStringUserId() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");

        this.mockMvc.perform(get("/users/а/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("{\"message\":\"Данный id: а, не целое число!\"}"));
    }

    @Test
    void checkFeedForSymbolUserId() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");

        this.mockMvc.perform(get("/users/@/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("{\"message\":\"Данный id: @, не целое число!\"}"));
    }

    @Test
    void addReviewAndCheckFeed() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");

        String json = "{\n" +
                "  \"content\": \"This film is soo bad.\",\n" +
                "  \"isPositive\": false,\n" +
                "  \"userId\": 1,\n" +
                "  \"filmId\": 7\n" +
                "}";

        this.mockMvc.perform(post("/reviews")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/users/1/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0]userId").value(1),
                        jsonPath("$.[0]eventType").value("REVIEW"),
                        jsonPath("$.[0]operation").value("ADD"),
                        jsonPath("$.[0]entityId").value(7)
                );
    }

    @Test
    void updateReviewAndCheckFeed() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");

        String json = "{\n" +
                "  \"content\": \"This film is soo bad.\",\n" +
                "  \"isPositive\": false,\n" +
                "  \"userId\": 1,\n" +
                "  \"filmId\": 5\n" +
                "}";

        this.mockMvc.perform(post("/reviews")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isCreated());

        json = "{\n" +
                "  \"reviewId\": 5,\n" +
                "  \"content\": \"This film is not too bad.\",\n" +
                "  \"isPositive\": true,\n" +
                "  \"userId\": 1,\n" +
                "  \"filmId\": 5,\n" +
                "  \"useful\": 10\n" +
                "}";

        this.mockMvc.perform(put("/reviews")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/1/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(2),
                        jsonPath("$.[1]userId").value(1),
                        jsonPath("$.[1]eventType").value("REVIEW"),
                        jsonPath("$.[1]operation").value("UPDATE"),
                        jsonPath("$.[1]entityId").value(5)
                );
    }

    @Test
    void deleteReviewAndCheckFeed() throws Exception {
        upData("src/test/resources/files/filmslist.txt", "/films");
        upData("src/test/resources/files/userslist.txt", "/users");

        String json = "{\n" +
                "  \"content\": \"This film is soo bad.\",\n" +
                "  \"isPositive\": false,\n" +
                "  \"userId\": 1,\n" +
                "  \"filmId\": 6\n" +
                "}";

        this.mockMvc.perform(post("/reviews")
                        .content(json)
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isCreated());

        this.mockMvc.perform(delete("/reviews/6")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/1/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json; charset=utf-8"),
                        jsonPath("$.length()").value(2),
                        jsonPath("$.[1]userId").value(1),
                        jsonPath("$.[1]eventType").value("REVIEW"),
                        jsonPath("$.[1]operation").value("REMOVE"),
                        jsonPath("$.[1]entityId").value(6)
                );
    }
}