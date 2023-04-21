package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/data/review/sql/init-data.sql")
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ReviewControllerTest {

    private final String basePath = "http://localhost:8080/reviews";
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/successfullySaveReview.csv", delimiter = '|')
    public void successfullySaveReview(String fileJson, String expectedResponse) throws Exception {
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/failSaveReview.csv", delimiter = '|')
    public void failSaveReview(String fileJson, String expectedResponse, int status) throws Exception {
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @Test
    public void successfullyUpdateReview() throws Exception {
        String response = mockMvc.perform(get(basePath + "/100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();
        mockMvc.perform(put(basePath)
                        .content(response)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/failUpdateReview.csv", delimiter = '|')
    public void failUpdateReview(String fileJson, String expectedResponse) throws Exception {
        mockMvc.perform(get(basePath)).andDo(print());
        mockMvc.perform(put(basePath)
                        .content(fileJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @Test
    public void successfullyDeleteReview() throws Exception {
        String expectedResult = "Отзыв с id(100) успешно удален!";
        expectedResult = String.format(expectedResult, 100);
        mockMvc.perform(delete(basePath + "/" + 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResult));
    }

    @Test
    public void failDeleteReview() throws Exception {
        String response = String.format("{\"message\":\"Отзыв с id(%d) не найден!\"}", Integer.MAX_VALUE);
        mockMvc.perform(delete(basePath + "/" + Integer.MAX_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    @Test
    public void successfullyGetReview() throws Exception {
        String response = "{\"reviewId\":100,\"content\":\"Some content 1\",\"isPositive\":true," +
                "\"userId\":100,\"filmId\":100,\"useful\":0}";
        mockMvc.perform(get(basePath + "/" + 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    @Test
    public void failGetReview() throws Exception {
        String expectedResponse = String.format("Отзыв с id(%d) не найден!", Integer.MAX_VALUE);
        mockMvc.perform(get(basePath + "/" + Integer.MAX_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/successfullyGetReviews.csv", delimiter = '|')
    public void successfullyGetReviews(String expectedResponse, String requestParams) throws Exception {
        mockMvc.perform(get(basePath + requestParams)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @Test
    public void successfullyAddLike() throws Exception {
        String pathVars = "/100/like/101";
        String expectedResponse = "Пользователь с id(101) успешно лайкнул отзыв с id(100)!";
        mockMvc.perform(put(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/failAddLike.csv", delimiter = '|')
    public void failAddLike(String pathVars, String expectedResponse) throws Exception {
        mockMvc.perform(put(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @Test
    public void successfullyAddDislike() throws Exception {
        String pathVars = "/100/dislike/101";
        String expectedResponse = "Пользователь с id(101) успешно дизлайкнул отзыв с id(100)!";
        mockMvc.perform(put(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/failAddDislike.csv", delimiter = '|')
    public void failAddDislike(String pathVars, String expectedResponse) throws Exception {
        mockMvc.perform(put(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @Test
    public void successDeleteLike() throws Exception {
        String pathVars = "/100/like/101";
        String expectedResponse = "Пользователь с id(101) успешно удалил лайк у отзыва с id(100)!";
        mockMvc.perform(put(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(delete(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/failDeleteDislike.csv", delimiter = '|')
    public void failDeleteLike(String pathVars, String expectedResponse) throws Exception {
        mockMvc.perform(delete(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @Test
    public void successfullyDeleteDislike() throws Exception {
        String pathVars = "/100/dislike/101";
        String expectedResponse = "Пользователь с id(101) успешно удалил дизлайк у отзыва с id(100)!";
        mockMvc.perform(put(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(delete(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/review/failDeleteDislike.csv", delimiter = '|')
    public void failDeleteDislike(String pathVars, String expectedResponse) throws Exception {
        mockMvc.perform(delete(basePath + pathVars)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }
}
