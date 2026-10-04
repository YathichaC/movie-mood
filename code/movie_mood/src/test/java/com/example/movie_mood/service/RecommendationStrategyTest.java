package com.example.movie_mood.service;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.strategy.MoodStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RecommendationStrategyTest {

    @Test
    @DisplayName("Should filter out disliked genres and calculate match score")
    public void testRecommendationLogic() {
        MovieService mockMovieService = new MovieService() {
            @Override
            public List<Movie> browseMovies() {
                Movie m1 = new Movie();
                m1.setTitle("Inside Out 2");
                m1.setRating(8.0);
                m1.setGenreIds(List.of(16, 35));

                Movie m2 = new Movie();
                m2.setTitle("A Quiet Place: Day One");
                m2.setRating(7.0);
                m2.setGenreIds(List.of(27, 53));

                return List.of(m1, m2);
            }

            @Override
            public List<Movie> searchMovies(String keyword) { return List.of(); }

            @Override
            public Movie getMovieDetails(Long tmdbMovieId) { return null; }
        };

        MoodStrategy strategy = new MoodStrategy(mockMovieService);
        List<Integer> dislikedGenres = List.of(27);
        List<Movie> results = strategy.recommend(Mood.HAPPY, dislikedGenres);

        Assertions.assertEquals(1, results.size(), "Disliked horror genre should be excluded");
        Assertions.assertEquals("Inside Out 2", results.get(0).getTitle());
        Assertions.assertNotNull(results.get(0).getMatchScore(), "Match score should not be null");
        
        System.out.println("====== TEST PASSED ======");
        System.out.println("Recommended Movie: " + results.get(0).getTitle());
        System.out.println("Match Score: " + results.get(0).getMatchScore() + "%");
    }
}