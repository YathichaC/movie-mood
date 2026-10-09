package com.example.movie_mood.facade;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.MovieService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class MovieDetailFacadeTest {

    @Test
    void shouldGetMovieDetailsFromMovieService() {

        MovieService movieService =
                mock(MovieService.class);

        MovieDetailFacade facade =
                new MovieDetailFacade(movieService);

        Movie movie = new Movie();
        movie.setTmdbMovieId("550");
        movie.setTitle("Test Movie");

        when(movieService.getMovieDetails("550"))
                .thenReturn(movie);

        Movie result =
                facade.getMovieDetails("550");

        assertSame(movie, result);

        verify(movieService, times(1))
                .getMovieDetails("550");
    }
}