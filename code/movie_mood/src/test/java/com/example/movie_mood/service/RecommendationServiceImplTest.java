
package com.example.movie_mood.service;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.MoviePage;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    @Mock
    private MovieProvider movieProvider;

    @Mock
    private MoodGenreMapper moodGenreMapper;

    @Mock
    private UserPreferenceService userPreferenceService;

    private RecommendationServiceImpl recommendationService;

    private final UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationServiceImpl(
                movieProvider,
                moodGenreMapper,
                userPreferenceService);
    }

    @Test
    @DisplayName("Should exclude movies with user's disliked genres")
    void shouldExcludeDislikedGenres() {

        Mood mood = Mood.HAPPY;

        Movie allowedMovie = createMovie(
                "1", "Comedy Movie", 8.5,
                LocalDate.of(2025, 1, 1), List.of(35));

        Movie dislikedMovie = createMovie(
                "2", "Horror Movie", 9.0,
                LocalDate.of(2025, 2, 1), List.of(27, 35));

        when(moodGenreMapper.getGenreIds(mood))
                .thenReturn(List.of(35));

        when(movieProvider.discoverMovies(
                List.of(35), null, null, null, null, 1))
                .thenReturn(new MoviePage(
                        List.of(allowedMovie, dislikedMovie),
                        1, 1, 2));

        when(userPreferenceService.getDislikedGenreIds(userId))
                .thenReturn(List.of("27"));

        List<Movie> results = recommendationService.getRecommendations(mood, userId);

        assertEquals(1, results.size());
        assertEquals("Comedy Movie", results.get(0).getTitle());

        verify(userPreferenceService).getDislikedGenreIds(userId);
        verify(movieProvider).discoverMovies(
                List.of(35), null, null, null, null, 1);
    }

    @Test
    @DisplayName("Should sort by rating and then release date")
    void shouldSortRecommendations() {

        Mood mood = Mood.HAPPY;

        Movie olderMovie = createMovie(
                "1", "Older Movie", 8.5,
                LocalDate.of(2024, 1, 1), List.of(35));

        Movie newerMovie = createMovie(
                "2", "Newer Movie", 8.5,
                LocalDate.of(2025, 1, 1), List.of(35));

        Movie topRatedMovie = createMovie(
                "3", "Top Rated", 9.0,
                LocalDate.of(2023, 1, 1), List.of(35));

        when(moodGenreMapper.getGenreIds(mood))
                .thenReturn(List.of(35));

        when(movieProvider.discoverMovies(
                List.of(35), null, null, null, null, 1))
                .thenReturn(new MoviePage(
                        List.of(olderMovie, newerMovie, topRatedMovie),
                        1, 1, 3));

        when(userPreferenceService.getDislikedGenreIds(userId))
                .thenReturn(List.of());

        List<Movie> results = recommendationService.getRecommendations(mood, userId);

        assertEquals(
                List.of("Top Rated", "Newer Movie", "Older Movie"),
                results.stream().map(Movie::getTitle).toList());
    }

    @Test
    @DisplayName("Should return at most 10 movies")
    void shouldLimitRecommendationsToTen() {

        Mood mood = Mood.HAPPY;

        List<Movie> movies = java.util.stream.IntStream
                .rangeClosed(1, 15)
                .mapToObj(i -> createMovie(
                        String.valueOf(i),
                        "Movie " + i,
                        8.0,
                        LocalDate.of(2025, 1, 1),
                        List.of(35)))
                .toList();

        when(moodGenreMapper.getGenreIds(mood))
                .thenReturn(List.of(35));

        when(movieProvider.discoverMovies(
                List.of(35), null, null, null, null, 1))
                .thenReturn(new MoviePage(
                        movies,
                        1, 1, 15));

        when(userPreferenceService.getDislikedGenreIds(userId))
                .thenReturn(List.of());

        List<Movie> results = recommendationService.getRecommendations(mood, userId);

        assertEquals(10, results.size());
    }

    private Movie createMovie(
            String id,
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

    @Test
    @DisplayName("Should fetch next page to fill 10 recommendations after filtering")
    void shouldFetchNextPageToFillTenRecommendations() {

        Mood mood = Mood.HAPPY;

        List<Movie> firstPage = java.util.stream.IntStream
                .rangeClosed(1, 10)
                .mapToObj(i -> createMovie(
                        String.valueOf(i),
                        "Horror Movie " + i,
                        8.0,
                        LocalDate.of(2025, 1, 1),
                        List.of(27, 35)))
                .toList();

        List<Movie> secondPage = java.util.stream.IntStream
                .rangeClosed(11, 20)
                .mapToObj(i -> createMovie(
                        String.valueOf(i),
                        "Comedy Movie " + i,
                        8.0,
                        LocalDate.of(2025, 1, 1),
                        List.of(35)))
                .toList();

        when(moodGenreMapper.getGenreIds(mood))
                .thenReturn(List.of(35));

        when(userPreferenceService.getDislikedGenreIds(userId))
                .thenReturn(List.of("27"));

        when(movieProvider.discoverMovies(
                List.of(35), null, null, null, null, 1))
                .thenReturn(new MoviePage(firstPage, 1, 2, 20));

        when(movieProvider.discoverMovies(
                List.of(35), null, null, null, null, 2))
                .thenReturn(new MoviePage(secondPage, 2, 2, 20));

        List<Movie> results = recommendationService.getRecommendations(mood, userId);

        assertEquals(10, results.size());

        assertTrue(results.stream()
                .noneMatch(movie -> movie.getGenreIds().contains(27)));

        verify(movieProvider).discoverMovies(
                List.of(35), null, null, null, null, 1);

        verify(movieProvider).discoverMovies(
                List.of(35), null, null, null, null, 2);
    }
}
