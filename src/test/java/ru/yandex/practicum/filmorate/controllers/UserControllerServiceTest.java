package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.AfterEach;
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
public class UserControllerServiceTest {

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
    void user1AndUser2Friends() throws Exception {
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}", "1", "2"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/{id}/friends", "1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(2),
                        jsonPath("$[0].name").value("Nick Name")
                );
    }

    @Test
    void user3IsACommonFriendUser1AndUser2() throws Exception {
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}", "1", "3"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}", "2", "3"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/{id}/friends/common/{friendId}", "1", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(3),
                        jsonPath("$[0].name").value("Petr Name")
                );
    }

    @Test
    void user1AndUser2WithoutCommonFriends() throws Exception {
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}", "1", "2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}", "2", "1"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/{id}/friends/common/{friendId}", "1", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }

    @Test
    void user1And2RemoveFriend() throws Exception {
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}", "1", "2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/users/{id}/friends/{friendId}", "1", "2"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/{id}/friends", "1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
        this.mockMvc.perform(get("/users/{id}/friends", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }

    @Test
    void getRecommendationWithoutLikes() throws Exception {
        this.mockMvc.perform(get("/users/{id}/recommendations", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRecommendationWithoutSomeLikes() throws Exception {
        initLikes();
        this.mockMvc.perform(delete("/films/{id}/like/{userId}", "2", "2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/films/{id}/like/{userId}", "5", "4"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/users/{id}/recommendations", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRecommendationUser1() throws Exception {
        initLikes();
        this.mockMvc.perform(get("/users/{id}/recommendations", "1"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(4),
                        jsonPath("$[0].name").value("New film #4")
                );
    }

    private void initLikes() throws Exception {
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "1", "1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "3", "1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "2", "2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "5", "2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "1", "3"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "2", "3"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "4", "3"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "2", "4"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "3", "4"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "5", "4"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "1", "5"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "3", "5"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/{id}/like/{userId}", "4", "5"))
                .andExpect(status().isOk());
    }

}