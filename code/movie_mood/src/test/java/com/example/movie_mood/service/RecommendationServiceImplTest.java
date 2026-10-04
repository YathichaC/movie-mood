package com.example.movie_mood.service;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.impl.RecommendationServiceImpl;
import com.example.movie_mood.strategy.MoodStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    @Mock
    private MoodStrategy moodStrategy;

    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationServiceImpl(moodStrategy);
    }

    @Test
    @DisplayName("Should delegate recommendation call to MoodStrategy")
    void testGetRecommendations() {
        Mood mood = Mood.HAPPY;
        List<Integer> disliked = List.of(27);
        Movie movie = new Movie();
        movie.setTitle("Inside Out 2");

        when(moodStrategy.recommend(mood, disliked)).thenReturn(List.of(movie));

        List<Movie> results = recommendationService.getRecommendations(mood, disliked);

        assertEquals(1, results.size());
        assertEquals("Inside Out 2", results.get(0).getTitle());
        verify(moodStrategy).recommend(mood, disliked);
    }
}