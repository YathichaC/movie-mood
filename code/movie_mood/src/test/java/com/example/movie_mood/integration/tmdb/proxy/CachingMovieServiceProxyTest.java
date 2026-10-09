package com.example.movie_mood.integration.tmdb.proxy;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.MoviePage;
import com.example.movie_mood.integration.tmdb.TmdbMovieAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        movie.setTmdbMovieId("550");
        movie.setTitle("Test Movie");

        when(movieAdapter.getMovie("550"))
                .thenReturn(movie);

        Movie firstResult = proxy.getMovie("550");
        Movie secondResult = proxy.getMovie("550");

        assertSame(firstResult, secondResult);

        verify(movieAdapter, times(1))
                .getMovie("550");
    }

    @Test
    void shouldReturnCachedPopularMoviesPageWhenRequestedMoreThanOnce() {

        MoviePage firstPage = new MoviePage(List.of(new Movie()), 1, 1, 1);
        MoviePage secondPage = new MoviePage(List.of(new Movie()), 1, 1, 1);

        when(movieAdapter.getPopularMovies(1))
                .thenReturn(firstPage, secondPage);

        MoviePage firstResult = proxy.getPopularMovies(1);
        MoviePage secondResult = proxy.getPopularMovies(1);

        assertSame(firstResult, secondResult);

        verify(movieAdapter, times(1))
                .getPopularMovies(1);
    }

    @Test
    void shouldTrackCacheHitAndMissMetrics() {
        Movie movie = new Movie();
        movie.setTmdbMovieId("550");

        MoviePage page = new MoviePage(List.of(new Movie()), 1, 1, 1);

        when(movieAdapter.getMovie("550")).thenReturn(movie);
        when(movieAdapter.getPopularMovies(1)).thenReturn(page);

        proxy.getMovie("550");
        proxy.getMovie("550");
        proxy.getPopularMovies(1);
        proxy.getPopularMovies(1);

        CachingMovieServiceProxy.CacheMetrics metrics = proxy.getCacheMetrics();

        assertEquals(1L, metrics.movieHits());
        assertEquals(1L, metrics.movieMisses());
        assertEquals(1L, metrics.popularHits());
        assertEquals(1L, metrics.popularMisses());
    }

    @Test
    void shouldCacheSameMovieDetailAcrossRepeatedLookups() {
        Movie movie = new Movie();
        movie.setTmdbMovieId("550");
        movie.setTitle("The Matrix");

        when(movieAdapter.getMovie("550")).thenReturn(movie);

        Movie first = proxy.getMovie("550");
        Movie second = proxy.getMovie("550");

        assertSame(movie, first);
        assertSame(movie, second);
        verify(movieAdapter, times(1)).getMovie("550");

        CachingMovieServiceProxy.CacheMetrics metrics = proxy.snapshotAndResetCacheMetrics();
        assertEquals(1L, metrics.movieHits());
        assertEquals(1L, metrics.movieMisses());
    }
}