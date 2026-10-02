package com.example.movie_mood.service;


import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class RecommendationServiceImplTest {

    @Mock
    private MovieProvider movieProvider;

    private MoodGenreMapper moodGenreMapper;
    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        moodGenreMapper = new MoodGenreMapper();

        recommendationService = new RecommendationServiceImpl(
                movieProvider,
                moodGenreMapper
        );
    }

    @Test
    void shouldUseMoodGenresToDiscoverMovies() {

        when(movieProvider.discoverMoviesByGenres(anyList()))
                .thenReturn(List.of());

        recommendationService.getRecommendations(
                Mood.HAPPY,
                List.of()
        );

        verify(movieProvider).discoverMoviesByGenres(
                moodGenreMapper.getGenreIds(Mood.HAPPY)
        );
    }

    @Test
    void shouldExcludeMoviesWithDislikedGenres() {

        Movie allowedMovie = createMovie(
                1L,
                "Comedy Movie",
                8.0,
                LocalDate.of(2025, 1, 1),
                List.of(35)
        );

        Movie dislikedMovie = createMovie(
                2L,
                "Horror Movie",
                9.0,
                LocalDate.of(2025, 1, 1),
                List.of(27)
        );

        when(movieProvider.discoverMoviesByGenres(anyList()))
                .thenReturn(List.of(allowedMovie, dislikedMovie));

        List<Movie> result =
                recommendationService.getRecommendations(
                        Mood.HAPPY,
                        List.of(27)
                );

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getTmdbMovieId());
    }

    @Test
    void shouldSortMoviesByRatingDescending() {

        Movie lowerRated = createMovie(
                1L,
                "Movie A",
                7.0,
                LocalDate.of(2025, 1, 1),
                List.of(35)
        );

        Movie higherRated = createMovie(
                2L,
                "Movie B",
                9.0,
                LocalDate.of(2025, 1, 1),
                List.of(35)
        );

        when(movieProvider.discoverMoviesByGenres(anyList()))
                .thenReturn(List.of(lowerRated, higherRated));

        List<Movie> result =
                recommendationService.getRecommendations(
                        Mood.HAPPY,
                        List.of()
                );

        assertEquals(2L, result.get(0).getTmdbMovieId());
        assertEquals(1L, result.get(1).getTmdbMovieId());
    }

    @Test
    void shouldSortNewerMovieFirstWhenRatingsAreEqual() {

        Movie olderMovie = createMovie(
                1L,
                "Old Movie",
                8.0,
                LocalDate.of(2024, 1, 1),
                List.of(35)
        );

        Movie newerMovie = createMovie(
                2L,
                "New Movie",
                8.0,
                LocalDate.of(2026, 1, 1),
                List.of(35)
        );

        when(movieProvider.discoverMoviesByGenres(anyList()))
                .thenReturn(List.of(olderMovie, newerMovie));

        List<Movie> result =
                recommendationService.getRecommendations(
                        Mood.HAPPY,
                        List.of()
                );

        assertEquals(2L, result.get(0).getTmdbMovieId());
        assertEquals(1L, result.get(1).getTmdbMovieId());
    }

    @Test
    void shouldReturnAtMostTenMovies() {

        List<Movie> movies = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            movies.add(
                    createMovie(
                            (long) i,
                            "Movie " + i,
                            (double) i,
                            LocalDate.of(2025, 1, 1),
                            List.of(35)
                    )
            );
        }

        when(movieProvider.discoverMoviesByGenres(anyList()))
                .thenReturn(movies);

        List<Movie> result =
                recommendationService.getRecommendations(
                        Mood.HAPPY,
                        List.of()
                );

        assertEquals(10, result.size());
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