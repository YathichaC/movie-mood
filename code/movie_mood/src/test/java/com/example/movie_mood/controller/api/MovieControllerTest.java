package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.exception.GlobalExceptionHandler;
import com.example.movie_mood.facade.MovieDetailFacade;
import com.example.movie_mood.mapper.MovieMapper;
import com.example.movie_mood.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MovieControllerTest {

    private MovieService movieService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        movieService = mock(MovieService.class);
        MovieDetailFacade movieDetailFacade =
                mock(MovieDetailFacade.class);
        MovieMapper movieMapper = new MovieMapper();

        MovieController controller = new MovieController(
                movieService,
                movieDetailFacade,
                movieMapper
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void filterMoviesByGenre_shouldReturnMovies() throws Exception {
        Movie movie = new Movie();
        movie.setTmdbMovieId(1L);
        movie.setTitle("Comedy Movie");
        movie.setRating(8.0);
        movie.setGenreIds(List.of(35));

        when(movieService.filterMoviesByGenre(35))
                .thenReturn(List.of(movie));

        mockMvc.perform(
                        get("/api/movies/filter")
                                .param("genreId", "35")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tmdbMovieId").value(1))
                .andExpect(jsonPath("$[0].title").value("Comedy Movie"))
                .andExpect(jsonPath("$[0].rating").value(8.0))
                .andExpect(jsonPath("$[0].genreIds[0]").value(35));

        verify(movieService).filterMoviesByGenre(35);
    }
}