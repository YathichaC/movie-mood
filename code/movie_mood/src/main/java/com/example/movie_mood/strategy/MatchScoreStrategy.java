package com.example.movie_mood.strategy;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;

public interface MatchScoreStrategy {
    double calculateScore(Movie movie, Mood mood);
}