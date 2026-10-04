package com.example.movie_mood.service;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.mapper.MoodGenreMapper;
import com.example.movie_mood.strategy.DefaultMatchScoreStrategy;
import com.example.movie_mood.strategy.MoodFocusedStrategy;
import com.example.movie_mood.strategy.TopRatedStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchScoreStrategyTest {

    private MoodGenreMapper moodGenreMapper;
    private DefaultMatchScoreStrategy defaultStrategy;
    private MoodFocusedStrategy moodFocusedStrategy;
    private TopRatedStrategy topRatedStrategy;

    @BeforeEach
    void setUp() {
        moodGenreMapper = new MoodGenreMapper();
        defaultStrategy = new DefaultMatchScoreStrategy(moodGenreMapper);
        moodFocusedStrategy = new MoodFocusedStrategy(moodGenreMapper);
        topRatedStrategy = new TopRatedStrategy(moodGenreMapper);
    }

    @Test
    @DisplayName("Default Strategy: Rating 8.0 with 1 mood genre match = 97.0")
    void testDefaultStrategy() {
        Movie movie = new Movie();
        movie.setRating(8.0);
        movie.setGenreIds(List.of(35)); // 35: Comedy (HAPPY)

        double score = defaultStrategy.calculateScore(movie, Mood.HAPPY);
        assertEquals(97.0, score, 0.01);
    }

    @Test
    @DisplayName("Mood Focused Strategy: Rating 8.0 with 2 mood genre matches = 96.0")
    void testMoodFocusedStrategy() {
        Movie movie = new Movie();
        movie.setRating(8.0);
        movie.setGenreIds(List.of(35, 16)); // Comedy & Animation (Both in HAPPY)

        double score = moodFocusedStrategy.calculateScore(movie, Mood.HAPPY);
        assertEquals(96.0, score, 0.01);
    }

    @Test
    @DisplayName("Top Rated Strategy: Rating 8.0 with 1 mood genre match = 88.0")
    void testTopRatedStrategy() {
        Movie movie = new Movie();
        movie.setRating(8.0);
        movie.setGenreIds(List.of(35)); // Comedy (HAPPY)

        double score = topRatedStrategy.calculateScore(movie, Mood.HAPPY);
        assertEquals(88.0, score, 0.01);
    }
}