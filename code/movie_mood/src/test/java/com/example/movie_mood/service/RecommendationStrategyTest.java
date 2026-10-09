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
                Movie m1 = new Movie();
                m1.setTitle("Inside Out 2");
                m1.setRating(8.0);
                m1.setGenreIds(List.of(16, 35));

                Movie m2 = new Movie();
                m2.setTitle("A Quiet Place: Day One");
                m2.setRating(7.0);
                m2.setGenreIds(List.of(27, 53));

                return new MoviePage(
                        List.of(m1, m2),
                        page,
                        1,
                        2);
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

        Assertions.assertEquals(
                1,
                results.size(),
                "Disliked horror genre should be excluded");

        Assertions.assertEquals(
                "Inside Out 2",
                results.get(0).getTitle());

        Assertions.assertNotNull(
                results.get(0).getMatchScore(),
                "Match score should not be null");

        System.out.println("====== TEST PASSED ======");
        System.out.println(
                "Recommended Movie: " + results.get(0).getTitle());
        System.out.println(
                "Match Score: " + results.get(0).getMatchScore() + "%");
    }
}