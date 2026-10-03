package com.example.movie_mood.integration.tmdb.proxy;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.TmdbMovieAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class CachingMovieServiceProxyTest {

    private TmdbMovieAdapter movieAdapter;
    private CachingMovieServiceProxy proxy;

    @BeforeEach
    void setUp() {
        movieAdapter = mock(TmdbMovieAdapter.class);
        proxy = new CachingMovieServiceProxy(movieAdapter);
    }

    @Test
    void shouldReturnCachedMovieWhenRequestedMoreThanOnce() {

        Movie movie = new Movie();
        movie.setTmdbMovieId(550L);
        movie.setTitle("Test Movie");

        when(movieAdapter.getMovie(550L))
                .thenReturn(movie);

        Movie firstResult = proxy.getMovie(550L);
        Movie secondResult = proxy.getMovie(550L);

        assertSame(firstResult, secondResult);

        verify(movieAdapter, times(1))
                .getMovie(550L);
    }
}