package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

        private MovieProvider movieProvider;
        private MoodGenreMapper moodGenreMapper;
        private MovieServiceImpl movieService;

        @BeforeEach
        void setUp() {
                movieProvider = mock(MovieProvider.class);
                moodGenreMapper = mock(MoodGenreMapper.class);

                movieService = new MovieServiceImpl(
                                movieProvider,
                                moodGenreMapper);
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
                movie.setTmdbMovieId(2L);
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
                Movie movie = new Movie();
                movie.setTmdbMovieId(10L);
                movie.setTitle("Popular Movie");

                List<Movie> expectedMovies = List.of(movie);

                when(movieProvider.getPopularMovies())
                                .thenReturn(expectedMovies);

                List<Movie> result = movieService.browseMovies();

                assertEquals(expectedMovies, result);
                verify(movieProvider).getPopularMovies();
        }

        @Test
        void searchMovies_shouldReturnMoviesFromProvider() {
                String keyword = "Batman";

                Movie movie = new Movie();
                movie.setTmdbMovieId(11L);
                movie.setTitle("Batman");

                List<Movie> expectedMovies = List.of(movie);

                when(movieProvider.searchMovies(keyword))
                                .thenReturn(expectedMovies);

                List<Movie> result = movieService.searchMovies(keyword);

                assertEquals(expectedMovies, result);
                verify(movieProvider).searchMovies(keyword);
        }

        @Test
        void getMovieDetails_shouldReturnMovieFromProvider() {
                Long tmdbMovieId = 550L;

                Movie expectedMovie = new Movie();
                expectedMovie.setTmdbMovieId(tmdbMovieId);
                expectedMovie.setTitle("Fight Club");

                when(movieProvider.getMovie(tmdbMovieId))
                                .thenReturn(expectedMovie);

                Movie result = movieService.getMovieDetails(tmdbMovieId);

                assertEquals(expectedMovie, result);
                verify(movieProvider).getMovie(tmdbMovieId);
        }
}