package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.RecommendationService;
import com.example.movie_mood.strategy.MoodStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final MoodStrategy moodStrategy;

    public RecommendationServiceImpl(MoodStrategy moodStrategy) {
        this.moodStrategy = moodStrategy;
    }

    @Override
    public List<Movie> getRecommendations(Mood mood, List<Integer> dislikedGenreIds) {
        return moodStrategy.recommend(mood, dislikedGenreIds);
    }
}