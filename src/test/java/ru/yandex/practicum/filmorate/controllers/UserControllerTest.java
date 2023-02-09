package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {


    private final MockMvc mockMvc;
    @Autowired
    UserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"Nick Name\",\n" +
                "  \"email\": \"mail@mail.ru\",\n" +
                "  \"birthday\": \"1946-08-20\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .content(json).header("Content-Type", "application/json");
        try {
            this.mockMvc.perform(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllUsers() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"email\":\"mail@mail.ru\",\"login\":\"dolore\",\"name\":\"Nick Name\",\"birthday\":\"1946-08-20\"},{\"id\":2,\"email\":\"mail@mail.ru\",\"login\":\"dolore\",\"name\":\"Nick Name\",\"birthday\":\"1946-08-20\"}]"));
    }

    @Test
    void addUser() throws Exception {
        this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"email\":\"mail@mail.ru\",\"login\":\"dolore\",\"name\":\"Nick Name\",\"birthday\":\"1946-08-20\"}"));
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
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/users")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder);
        this.mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\"}"));
    }

    @Test
    void addFailLoginUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"\",\n" +
                "  \"name\": \"est adipisicing\",\n" +
                "  \"email\": \"mail@yandex.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Login не может быть пустым или отсутствовать!\"}"));
    }

    @Test
    void addFailEmailUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"doloreUpdate\",\n" +
                "  \"name\": \"est adipisicing\",\n" +
                "  \"email\": \"mail_yandex.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Не корректный Email!\"}"));
    }

    @Test
    void addFailBirthDayEmailUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"2446-08-20\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Дата рождения не может отсутствовать или быть в будущем!\"}"));
    }

    @Test
    void addEmptyBirthDayEmailUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Дата рождения не может отсутствовать или быть в будущем!\"}"));
    }

    @Test
    void addWithoutNameUser() throws Exception {
        String json = "{\n" +
                "  \"login\": \"dolore\",\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"test@mail.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .content(json).header("Content-Type", "application/json");
        this.mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":9,\"email\":\"test@mail.ru\",\"login\":\"dolore\",\"name\":\"dolore\",\"birthday\":\"1976-09-20\"}"));
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
}