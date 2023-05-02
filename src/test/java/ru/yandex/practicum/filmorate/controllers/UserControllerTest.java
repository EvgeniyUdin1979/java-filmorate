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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql(scripts = {"file:src/test/resources/files/maindata.sql","file:src/test/resources/data/review/sql/cleanReview.sql"})
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
class UserControllerTest {
    @Autowired
    private  MockMvc mockMvc;

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
    void getAllUsers() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(5),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[2].id").value(3)
                );
    }

    @Test
    void updateUser() throws Exception {
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
    void deleteUser() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/users/1")
                .header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/users/1")).andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void addFriendAndCheckFeed() throws Exception {
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
        this.mockMvc.perform(get("/users/9999/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().string("{\"message\":\"Пользователь с данным id: 9999, не найден\"}"));
    }

    @Test
    void checkFeedForNegativeUserId() throws Exception {
        this.mockMvc.perform(get("/users/-1/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().string("{\"message\":\"Пользователь с данным id: -1, не найден\"}"));
    }

    @Test
    void checkFeedForStringUserId() throws Exception {
        this.mockMvc.perform(get("/users/а/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("{\"message\":\"Данный id: а, не целое число!\"}"));
    }

    @Test
    void checkFeedForSymbolUserId() throws Exception {
        this.mockMvc.perform(get("/users/@/feed")
                        .header("Content-Type", "application/json; charset=utf-8"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("{\"message\":\"Данный id: @, не целое число!\"}"));
    }

    @Test
    void addReviewAndCheckFeed() throws Exception {
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
                        jsonPath("$.[0]entityId").value(1)
                );
    }

    @Test
    void updateReviewAndCheckFeed() throws Exception {
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
                "  \"reviewId\": 1,\n" +
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
                        jsonPath("$.[1]entityId").value(1)
                );
    }

    @Test
    void deleteReviewAndCheckFeed() throws Exception {
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

        this.mockMvc.perform(delete("/reviews/1")
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
                        jsonPath("$.[1]entityId").value(1)
                );
    }
}