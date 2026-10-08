package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.example.movie_mood.domain.model.MoviePage;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

        private MovieProvider movieProvider;
        private MoodGenreMapper moodGenreMapper;
        private GenreRepository genreRepository;
        private MovieServiceImpl movieService;

        @BeforeEach
        void setUp() {
                movieProvider = mock(MovieProvider.class);
                moodGenreMapper = mock(MoodGenreMapper.class);
                genreRepository = mock(GenreRepository.class);

                movieService = new MovieServiceImpl(
                                movieProvider,
                                moodGenreMapper,
                                genreRepository);
        }

        @Test
        void filterMoviesByGenre_shouldReturnMoviesFromProvider() {
                Integer genreId = 35;

                Movie movie = new Movie();
                movie.setTmdbMovieId("1");
                movie.setTitle("Comedy Movie");
                movie.setGenreIds(List.of(35));

                List<Movie> expectedMovies = List.of(movie);

                when(movieProvider.discoverMoviesByGenres(List.of(genreId)))
                                .thenReturn(expectedMovies);

                List<Movie> result = movieService.filterMoviesByGenre(genreId);

                assertEquals(expectedMovies, result);

                verify(movieProvider)
                                .discoverMoviesByGenres(List.of(genreId));
        }

        @Test
        void filterMoviesByMood_shouldMapMoodToGenresAndReturnMovies() {
                Mood mood = Mood.HAPPY;

                List<Integer> genreIds = List.of(35, 16, 10751, 10402, 14);

                Movie movie = new Movie();
                movie.setTmdbMovieId("2");
                movie.setTitle("Happy Movie");
                movie.setGenreIds(List.of(35));

                List<Movie> expectedMovies = List.of(movie);

                when(moodGenreMapper.getGenreIds(mood))
                                .thenReturn(genreIds);

                when(movieProvider.discoverMoviesByGenres(genreIds))
                                .thenReturn(expectedMovies);

                List<Movie> result = movieService.filterMoviesByMood(mood);

                assertEquals(expectedMovies, result);

                verify(moodGenreMapper).getGenreIds(Mood.HAPPY);
                verify(movieProvider)
                                .discoverMoviesByGenres(genreIds);
        }

        @Test
        void browseMovies_shouldReturnPopularMoviesFromProvider() {
                int page = 1;

                Movie movie = new Movie();
                movie.setTmdbMovieId("10");
                movie.setTitle("Popular Movie");

                MoviePage expectedPage = new MoviePage(
                                List.of(movie),
                                1,
                                10,
                                200);

                when(movieProvider.getPopularMovies(page))
                                .thenReturn(expectedPage);

                MoviePage result = movieService.browseMovies(page);

                assertEquals(expectedPage, result);

                verify(movieProvider).getPopularMovies(page);
        }

        @Test
        void searchMovies_shouldReturnMoviesFromProvider() {
                String keyword = "Batman";
                int page = 1;

                Movie movie = new Movie();
                movie.setTmdbMovieId("11");
                movie.setTitle("Batman");

                MoviePage expectedPage = new MoviePage(
                                List.of(movie),
                                1,
                                1,
                                1);

                when(movieProvider.searchMovies(keyword, page))
                                .thenReturn(expectedPage);

                MoviePage result = movieService.searchMovies(keyword, page);

                assertEquals(expectedPage, result);

                verify(movieProvider).searchMovies(keyword, page);
        }

        @Test
        void getMovieDetails_shouldMapGenreIdsToGenreNames() {
                String tmdbMovieId = "550";

                Movie movie = new Movie();
                movie.setTmdbMovieId(tmdbMovieId);
                movie.setTitle("Fight Club");
                movie.setGenreIds(List.of(18, 53));

                Genre drama = new Genre("18", "Drama");
                Genre thriller = new Genre("53", "Thriller");

                when(movieProvider.getMovie(tmdbMovieId))
                                .thenReturn(movie);

                when(genreRepository.findById("18"))
                                .thenReturn(Optional.of(drama));

                when(genreRepository.findById("53"))
                                .thenReturn(Optional.of(thriller));

                Movie result = movieService.getMovieDetails(tmdbMovieId);

                assertEquals(movie, result);
                assertEquals(
                                List.of("Drama", "Thriller"),
                                result.getGenres());

                verify(movieProvider).getMovie(tmdbMovieId);
                verify(genreRepository).findById("18");
                verify(genreRepository).findById("53");
        }

        @Test
        void discoverMovies_shouldReturnMoviesFromProvider() {
                Integer genreId = 28;
                Integer startYear = 2020;
                Integer endYear = 2026;
                Double minRating = 7.0;
                String sortBy = "rating_desc";
                int page = 1;

                Movie movie = new Movie();
                movie.setTmdbMovieId("100");
                movie.setTitle("Action Movie");
                movie.setRating(8.5);
                movie.setGenreIds(List.of(28));

                MoviePage expectedPage = new MoviePage(
                                List.of(movie),
                                1,
                                5,
                                100);

                when(movieProvider.discoverMovies(
                                genreId,
                                startYear,
                                endYear,
                                minRating,
                                sortBy,
                                page))
                                .thenReturn(expectedPage);

                MoviePage result = movieService.discoverMovies(
                                genreId,
                                startYear,
                                endYear,
                                minRating,
                                sortBy,
                                page);

                assertEquals(expectedPage, result);

                verify(movieProvider).discoverMovies(
                                genreId,
                                startYear,
                                endYear,
                                minRating,
                                sortBy,
                                page);
        }

        @Test
        void discoverMovies_withLowestRating_shouldPassSortToProvider() {
                String sortBy = "rating_asc";
                int page = 1;

                MoviePage expectedPage = new MoviePage(
                                List.of(),
                                1,
                                1,
                                0);

                when(movieProvider.discoverMovies(
                                null,
                                null,
                                null,
                                null,
                                sortBy,
                                page))
                                .thenReturn(expectedPage);

                MoviePage result = movieService.discoverMovies(
                                null,
                                null,
                                null,
                                null,
                                sortBy,
                                page);

                assertEquals(expectedPage, result);

                verify(movieProvider).discoverMovies(
                                null,
                                null,
                                null,
                                null,
                                "rating_asc",
                                page);
        }

        @Test
        void discoverMovies_withInvalidYearRange_shouldThrowIllegalArgumentException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> movieService.discoverMovies(
                                                28,
                                                2026,
                                                2020,
                                                7.0,
                                                "rating_desc",
                                                1));

                assertEquals(
                                "startYear must not be greater than endYear",
                                exception.getMessage());

                verifyNoInteractions(movieProvider);
        }

        @Test
        void discoverMovies_withInvalidSortBy_shouldThrowIllegalArgumentException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> movieService.discoverMovies(
                                                28,
                                                2020,
                                                2026,
                                                7.0,
                                                "hello",
                                                1));

                assertEquals(
                                "Invalid sortBy value",
                                exception.getMessage());

                verifyNoInteractions(movieProvider);
        }
}