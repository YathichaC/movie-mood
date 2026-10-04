package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.exception.GlobalExceptionHandler;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecommendationControllerTest {

    private MockMvc mockMvc;
    private RecommendationService recommendationService;
    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {

        recommendationService =
                Mockito.mock(RecommendationService.class);

        movieMapper = new MovieMapper();

        RecommendationController controller =
                new RecommendationController(
                        recommendationService,
                        movieMapper
                );

        mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
    }

    @Test
    void shouldReturnRecommendationsByMood() throws Exception {

        Movie movie = createMovie(
                550L,
                "Test Movie",
                8.5,
                LocalDate.of(2025, 1, 1),
                List.of(35)
        );

        when(recommendationService.getRecommendations(
                eq(Mood.HAPPY),
                eq(List.of())
        )).thenReturn(List.of(movie));

        mockMvc.perform(
                        get("/api/recommendations")
                                .param("mood", "HAPPY")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].tmdbMovieId")
                                .value(550)
                )
                .andExpect(
                        jsonPath("$[0].title")
                                .value("Test Movie")
                )
                .andExpect(
                        jsonPath("$[0].rating")
                                .value(8.5)
                );

        verify(recommendationService)
                .getRecommendations(
                        Mood.HAPPY,
                        List.of()
                );
    }

    @Test
    void shouldPassDislikedGenreIdsToService() throws Exception {

        when(recommendationService.getRecommendations(
                eq(Mood.EXCITED),
                eq(List.of(53, 80))
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/recommendations")
                                .param("mood", "EXCITED")
                                .param(
                                        "dislikedGenreIds",
                                        "53",
                                        "80"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(recommendationService)
                .getRecommendations(
                        Mood.EXCITED,
                        List.of(53, 80)
                );
    }

        @Test
        void shouldReturnBadRequestWhenMoodIsInvalid()
        throws Exception {

    mockMvc.perform(
                    get("/api/recommendations")
                            .param("mood", "INVALID_MOOD")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message")
                    .value("Invalid value for parameter: mood"))
            .andExpect(jsonPath("$.path")
                    .value("/api/recommendations"));
}

        @Test
        void shouldReturnBadRequestWhenMoodIsMissing()
        throws Exception {

    mockMvc.perform(
                    get("/api/recommendations")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message")
                    .value("Missing required parameter: mood"))
            .andExpect(jsonPath("$.path")
                    .value("/api/recommendations"));
}

    private Movie createMovie(
            Long id,
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