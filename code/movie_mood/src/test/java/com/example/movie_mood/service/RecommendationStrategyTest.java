package com.example.movie_mood.service;

import com.example.movie_mood.domain.model.MoviePage;
import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.strategy.DefaultMatchScoreStrategy;
import com.example.movie_mood.strategy.MatchScoreStrategy;
import com.example.movie_mood.strategy.MoodStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.example.movie_mood.domain.model.Video;
import java.util.List;

public class RecommendationStrategyTest {

    @Test
    @DisplayName("Should filter out disliked genres and calculate match score")
    public void testRecommendationLogic() {

        MovieService mockMovieService = new MovieService() {
            @Override
            public MoviePage browseMovies(int page) {
                return new MoviePage(
                        List.of(),
                        page,
                        0,
                        0);
            }

            @Override
            public MoviePage searchMoviesWithFilters(
                    String keyword,
                    List<Integer> genreIds,
                    Integer startYear,
                    Integer endYear,
                    Double minRating,
                    String sortBy,
                    int page) {

                throw new UnsupportedOperationException(
                        "Not used in RecommendationStrategyTest");
            }

            @Override
            public MoviePage searchMovies(String keyword, int page) {
                return new MoviePage(
                        List.of(),
                        page,
                        0,
                        0);
            }

            @Override
            public MoviePage discoverMovies(
                    List<Integer> genreIds,
                    Integer startYear,
                    Integer endYear,
                    Double minRating,
                    String sortBy,
                    int page) {

                return new MoviePage(
                        List.of(),
                        page,
                        0,
                        0);
            }

            @Override
            public List<Movie> getMovieBatch(List<String> tmdbMovieIds) {
                return List.of();
            }

            @Override
            public Movie getMovieDetails(String tmdbMovieId) {
                return null;
            }

            @Override
            public Movie getMovieSummaryForHistory(String tmdbMovieId) {
                return null;
            }

            @Override
            public Video getMovieTrailer(String tmdbMovieId) {
                return null;
            }

            @Override
            public List<Movie> filterMoviesByGenre(Integer genreId) {
                return List.of();
            }

            @Override
            public List<Movie> filterMoviesByMood(Mood mood) {
                return List.of();
            }
        };

        MoodGenreMapper moodGenreMapper = new MoodGenreMapper();

        MatchScoreStrategy defaultStrategy = new DefaultMatchScoreStrategy(moodGenreMapper);

        MoodStrategy strategy = new MoodStrategy(mockMovieService, defaultStrategy);

        List<Movie> results = strategy.recommend(Mood.HAPPY, List.of(27));

        Assertions.assertEquals(2, results.size(),
                "Both non-horror movies should remain");

        Assertions.assertTrue(
                results.stream().noneMatch(movie -> movie.getGenreIds() != null
                        && movie.getGenreIds().stream()
                                .anyMatch(id -> "27".equals(String.valueOf(id)))),
                "Disliked horror genre should be excluded");

        Assertions.assertTrue(
                results.stream().anyMatch(movie -> "Inside Out 2".equals(movie.getTitle())));

        Assertions.assertTrue(
                results.stream().allMatch(movie -> movie.getMatchScore() != null),
                "Every recommended movie should have a match score");

        System.out.println("====== TEST PASSED ======");
        System.out.println(
                "Recommended Movie: " + results.get(0).getTitle());
        System.out.println(
                "Match Score: " + results.get(0).getMatchScore() + "%");
    }
}