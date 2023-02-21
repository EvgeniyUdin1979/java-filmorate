package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerServiceTest {


    private final MockMvc mockMvc;

    @Autowired
    UserControllerServiceTest(MockMvc mockMvc) {
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
        String[] strings = Files.readString(Path.of(urlData)).split("(?<=\\}),");
        for (String json : strings) {
            this.mockMvc.perform(post(urlRequest)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Test
    void user1AndUser2Friends() throws Exception {
        upData("src/test/resources/files/userslist.txt", "/users");
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}","1","2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}","2","1"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/{id}/friends","1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(2),
                        jsonPath("$[0].name").value("Nick Name")
                );
        this.mockMvc.perform(get("/users/{id}/friends","2"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json;charset=UTF-8"),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].name").value("Stas Name")
                );
    }

   @Test
    void user3IsACommonFriendUser1AndUser2() throws Exception{
       upData("src/test/resources/files/userslist.txt", "/users");
       this.mockMvc.perform(put("/users/{id}/friends/{friendId}","1","3"))
               .andExpect(status().isOk());
       this.mockMvc.perform(put("/users/{id}/friends/{friendId}","2","3"))
               .andExpect(status().isOk());

       this.mockMvc.perform(get("/users/{id}/friends/common/{friendId}","1","2"))
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
        upData("src/test/resources/files/userslist.txt", "/users");
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}","1","2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}","2","1"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/{id}/friends/common/{friendId}","1","2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }

    @Test
    void user1RemoveFriend() throws Exception {
        upData("src/test/resources/files/userslist.txt", "/users");
        this.mockMvc.perform(put("/users/{id}/friends/{friendId}","1","2"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/users/{id}/friends/{friendId}","1","2"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/users/{id}/friends","1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }

}