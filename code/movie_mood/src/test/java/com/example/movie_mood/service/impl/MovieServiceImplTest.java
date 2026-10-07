package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.entity.Genre;
import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                Movie movie = new Movie();
                movie.setTmdbMovieId("10");
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
                movie.setTmdbMovieId("11");
                movie.setTitle("Batman");

                List<Movie> expectedMovies = List.of(movie);

                when(movieProvider.searchMovies(keyword))
                                .thenReturn(expectedMovies);

                List<Movie> result = movieService.searchMovies(keyword);

                assertEquals(expectedMovies, result);
                verify(movieProvider).searchMovies(keyword);
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
}