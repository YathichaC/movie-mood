package com.example.movie_mood.exception;

import com.example.movie_mood.controller.api.MovieController;
import com.example.movie_mood.facade.MovieDetailFacade;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MovieDetailFacade movieDetailFacade;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MovieService movieService = mock(MovieService.class);
        movieDetailFacade = mock(MovieDetailFacade.class);

        MovieController controller = new MovieController(
                movieService,
                movieDetailFacade,
                new MovieMapper()
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn404WhenMovieNotFound() throws Exception {
        when(movieDetailFacade.getMovieDetails("999999999"))
                .thenThrow(new MovieNotFoundException("999999999"));

        mockMvc.perform(get("/api/v1/movies/999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Movie not found with TMDB ID: 999999999"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/movies/999999999"));
    }
}