package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

    private MovieProvider movieProvider;
    private MovieServiceImpl movieService;

    @BeforeEach
    void setUp() {
        movieProvider = mock(MovieProvider.class);
        movieService = new MovieServiceImpl(movieProvider);
    }

    @Test
    void filterMoviesByGenre_shouldReturnMoviesFromProvider() {
        Integer genreId = 35;

        Movie movie = new Movie();
        movie.setTmdbMovieId(1L);
        movie.setTitle("Comedy Movie");
        movie.setGenreIds(List.of(35));

        List<Movie> expectedMovies = List.of(movie);

        when(movieProvider.discoverMoviesByGenres(List.of(genreId)))
                .thenReturn(expectedMovies);

        List<Movie> result =
                movieService.filterMoviesByGenre(genreId);

        assertEquals(expectedMovies, result);

        verify(movieProvider)
                .discoverMoviesByGenres(List.of(genreId));
    }
}