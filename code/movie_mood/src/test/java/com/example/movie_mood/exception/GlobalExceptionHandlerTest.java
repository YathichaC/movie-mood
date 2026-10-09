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

        private MovieService movieService;
        private MovieDetailFacade movieDetailFacade;
        private GlobalExceptionHandler exceptionHandler;
        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                movieService = mock(MovieService.class);
                movieDetailFacade = mock(MovieDetailFacade.class);

                MovieController controller = new MovieController(
                                movieService,
                                movieDetailFacade,
                                new MovieMapper());

                exceptionHandler = new GlobalExceptionHandler();

                mockMvc = MockMvcBuilders
                                .standaloneSetup(controller)
                                .setControllerAdvice(exceptionHandler)
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

        @Test
        void shouldReturn409WhenGenreAlreadyExists() throws Exception {
                when(movieService.browseMovies(1))
                                .thenThrow(new GenreAlreadyExistsException("35"));

                mockMvc.perform(get("/api/v1/movies"))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409))
                                .andExpect(jsonPath("$.error").value("Conflict"))
                                .andExpect(jsonPath("$.message")
                                                .value("Genre already exists with id: 35"))
                                .andExpect(jsonPath("$.path")
                                                .value("/api/v1/movies"));
        }

        @Test
        void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {
                when(movieService.browseMovies(1))
                                .thenThrow(new RuntimeException("Database connection failed"));

                mockMvc.perform(get("/api/v1/movies"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.error")
                                                .value("Internal Server Error"))
                                .andExpect(jsonPath("$.message")
                                                .value("An unexpected error occurred"))
                                .andExpect(jsonPath("$.path")
                                                .value("/api/v1/movies"));
        }

        @Test
        void shouldReturn400WhenIllegalArgumentExceptionOccurs()
                        throws Exception {

                when(movieService.discoverMovies(
                                null,
                                null,
                                null,
                                null,
                                "rating_desc",
                                1))
                                .thenThrow(
                                                new IllegalArgumentException(
                                                                "Invalid discover request"));

                mockMvc.perform(
                                get("/api/v1/movies/discover")
                                                .param("sortBy", "rating_desc")
                                                .param("page", "1"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error")
                                                .value("Bad Request"))
                                .andExpect(jsonPath("$.message")
                                                .value("Invalid discover request"))
                                .andExpect(jsonPath("$.path")
                                                .value("/api/v1/movies/discover"));
        }
}