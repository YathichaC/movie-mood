package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.dto.TmdbGenreResponse;
import com.example.movie_mood.integration.tmdb.dto.TmdbMovieResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TmdbMovieAdapterTest {

    @Test
    void getMovieShouldMapPosterAndBackdropPaths() {
        // Arrange
        TmdbRestClient tmdbRestClient = mock(TmdbRestClient.class);
        TmdbMovieAdapter adapter = new TmdbMovieAdapter(tmdbRestClient);

        TmdbMovieResponse response = new TmdbMovieResponse();
        response.setId(123L);
        response.setTitle("Test Movie");
        response.setOverview("Test overview");
        response.setVoteAverage(8.5);
        response.setReleaseDate("2025-01-15");
        response.setGenreIds(List.of(28, 12));
        response.setPosterPath("/poster-test.jpg");
        response.setBackdropPath("/backdrop-test.jpg");

        when(tmdbRestClient.getMovie(123L)).thenReturn(response);

        // Act
        Movie movie = adapter.getMovie(123L);

        // Assert
        assertEquals(123L, movie.getTmdbMovieId());
        assertEquals("Test Movie", movie.getTitle());
        assertEquals("/poster-test.jpg", movie.getPosterPath());
        assertEquals("/backdrop-test.jpg", movie.getBackdropPath());
    }

    @Test
    void getMovieShouldMapDetailGenresToGenreIds() {
        // Arrange
        TmdbRestClient tmdbRestClient = mock(TmdbRestClient.class);
        TmdbMovieAdapter adapter = new TmdbMovieAdapter(tmdbRestClient);

        TmdbGenreResponse drama = new TmdbGenreResponse();
        drama.setId(18);
        drama.setName("Drama");

        TmdbGenreResponse thriller = new TmdbGenreResponse();
        thriller.setId(53);
        thriller.setName("Thriller");

        TmdbMovieResponse response = new TmdbMovieResponse();
        response.setId(550L);
        response.setTitle("Fight Club");
        response.setGenres(List.of(drama, thriller));

        when(tmdbRestClient.getMovie(550L)).thenReturn(response);

        // Act
        Movie movie = adapter.getMovie(550L);

        // Assert
        assertEquals(List.of(18, 53), movie.getGenreIds());
    }
}