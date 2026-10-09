
package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.exception.GlobalExceptionHandler;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecommendationControllerTest {

    private MockMvc mockMvc;
    private RecommendationService recommendationService;
    private MovieMapper movieMapper;

    private final UUID userId =
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {

        recommendationService = Mockito.mock(RecommendationService.class);
        movieMapper = new MovieMapper();

        RecommendationController controller =
                new RecommendationController(recommendationService, movieMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnRecommendationsByMood() throws Exception {

        Movie movie = createMovie(
                "550",
                "Test Movie",
                8.5,
                LocalDate.of(2025, 1, 1),
                List.of(35));

        when(recommendationService.getRecommendations(
                eq(Mood.HAPPY),
                eq(userId))).thenReturn(List.of(movie));

        mockMvc.perform(
                get("/api/v1/recommendations")
                        .param("mood", "HAPPY")
                        .principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tmdbMovieId").value("550"))
                .andExpect(jsonPath("$[0].title").value("Test Movie"))
                .andExpect(jsonPath("$[0].rating").value(8.5));

        verify(recommendationService)
                .getRecommendations(Mood.HAPPY, userId);
    }

    @Test
    void shouldPassAuthenticatedUserIdToService() throws Exception {

        when(recommendationService.getRecommendations(
                eq(Mood.EXCITED),
                eq(userId))).thenReturn(List.of());

        mockMvc.perform(
                get("/api/v1/recommendations")
                        .param("mood", "EXCITED")
                        .principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(recommendationService)
                .getRecommendations(Mood.EXCITED, userId);
    }

    @Test
    void shouldReturnBadRequestWhenMoodIsInvalid() throws Exception {

        mockMvc.perform(
                get("/api/v1/recommendations")
                        .param("mood", "INVALID_MOOD")
                        .principal(authentication()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid value for parameter: mood"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/recommendations"));
    }

    @Test
    void shouldReturnBadRequestWhenMoodIsMissing() throws Exception {

        mockMvc.perform(
                get("/api/v1/recommendations")
                        .principal(authentication()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Missing required parameter: mood"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/recommendations"));
    }

    private UsernamePasswordAuthenticationToken authentication() {
        return new UsernamePasswordAuthenticationToken(
                userId.toString(),
                null,
                List.of());
    }

    private Movie createMovie(
            String id,
            String title,
            Double rating,
            LocalDate releaseDate,
            List<Integer> genreIds) {

        Movie movie = new Movie();
        movie.setTmdbMovieId(id);
        movie.setTitle(title);
        movie.setRating(rating);
        movie.setReleaseDate(releaseDate);
        movie.setGenreIds(genreIds);

        return movie;
    }
}
