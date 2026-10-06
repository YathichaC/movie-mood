package com.example.movie_mood.controller.api;

import com.example.movie_mood.domain.enums.Mood;
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
    private MovieDetailFacade movieDetailFacade;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        movieService = mock(MovieService.class);
        movieDetailFacade = mock(MovieDetailFacade.class);
        MovieMapper movieMapper = new MovieMapper();

        MovieController controller = new MovieController(
                movieService,
                movieDetailFacade,
                movieMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void filterMoviesByGenre_shouldReturnMovies() throws Exception {
        Movie movie = new Movie();
        movie.setTmdbMovieId("1");
        movie.setTitle("Comedy Movie");
        movie.setRating(8.0);
        movie.setGenreIds(List.of(35));

        when(movieService.filterMoviesByGenre(35))
                .thenReturn(List.of(movie));

        mockMvc.perform(
                get("/api/v1/movies/filter")
                        .param("genreId", "35"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tmdbMovieId").value("1"))
                .andExpect(jsonPath("$[0].title").value("Comedy Movie"))
                .andExpect(jsonPath("$[0].rating").value(8.0))
                .andExpect(jsonPath("$[0].genreIds[0]").value(35));

        verify(movieService).filterMoviesByGenre(35);
    }

    @Test
    void filterMoviesByMood_shouldReturnMovies() throws Exception {
        Movie movie = new Movie();
        movie.setTmdbMovieId("2");
        movie.setTitle("Happy Movie");
        movie.setRating(7.5);
        movie.setGenreIds(List.of(35, 16));

        when(movieService.filterMoviesByMood(Mood.HAPPY))
                .thenReturn(List.of(movie));

        mockMvc.perform(
                get("/api/v1/movies/filter/mood")
                        .param("mood", "HAPPY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tmdbMovieId").value("2"))
                .andExpect(jsonPath("$[0].title").value("Happy Movie"))
                .andExpect(jsonPath("$[0].rating").value(7.5))
                .andExpect(jsonPath("$[0].genreIds[0]").value(35))
                .andExpect(jsonPath("$[0].genreIds[1]").value(16));

        verify(movieService)
                .filterMoviesByMood(Mood.HAPPY);
    }

    @Test
    void filterMoviesByMood_withInvalidMood_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                get("/api/v1/movies/filter/mood")
                        .param("mood", "ANGRY"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(movieService);
    }

    @Test
    void browseMovies_shouldReturnMovies() throws Exception {
        Movie movie = new Movie();
        movie.setTmdbMovieId("10");
        movie.setTitle("Popular Movie");
        movie.setRating(8.2);
        movie.setGenreIds(List.of(28, 12));

        when(movieService.browseMovies())
                .thenReturn(List.of(movie));

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tmdbMovieId").value("10"))
                .andExpect(jsonPath("$[0].title").value("Popular Movie"))
                .andExpect(jsonPath("$[0].rating").value(8.2))
                .andExpect(jsonPath("$[0].genreIds[0]").value(28))
                .andExpect(jsonPath("$[0].genreIds[1]").value(12));

        verify(movieService).browseMovies();
    }

    @Test
    void searchMovies_shouldReturnMatchingMovies() throws Exception {
        Movie movie = new Movie();
        movie.setTmdbMovieId("11");
        movie.setTitle("Batman");
        movie.setRating(8.0);
        movie.setGenreIds(List.of(28));

        when(movieService.searchMovies("Batman"))
                .thenReturn(List.of(movie));

        mockMvc.perform(
                get("/api/v1/movies/search")
                        .param("keyword", "Batman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tmdbMovieId").value("11"))
                .andExpect(jsonPath("$[0].title").value("Batman"))
                .andExpect(jsonPath("$[0].rating").value(8.0));

        verify(movieService).searchMovies("Batman");
    }

    @Test
    void getMovieDetails_shouldReturnMovie() throws Exception {
        Movie movie = new Movie();
        movie.setTmdbMovieId("550");
        movie.setTitle("Fight Club");
        movie.setRating(8.4);
        movie.setGenreIds(List.of(18, 53));
        movie.setPosterPath("/fight-club-poster.jpg");
        movie.setBackdropPath("/fight-club-backdrop.jpg");

        when(movieDetailFacade.getMovieDetails("550"))
                .thenReturn(movie);

        mockMvc.perform(get("/api/v1/movies/550"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tmdbMovieId").value("550"))
                .andExpect(jsonPath("$.title").value("Fight Club"))
                .andExpect(jsonPath("$.rating").value(8.4))
                .andExpect(jsonPath("$.genreIds[0]").value(18))
                .andExpect(jsonPath("$.genreIds[1]").value(53))
                .andExpect(jsonPath("$.posterPath")
                        .value("/fight-club-poster.jpg"))
                .andExpect(jsonPath("$.backdropPath")
                        .value("/fight-club-backdrop.jpg"));

        verify(movieDetailFacade).getMovieDetails("550");
        verifyNoInteractions(movieService);
    }
}
