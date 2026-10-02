package com.example.movie_mood.service;


import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;

import java.util.List;

public interface RecommendationService {

    List<Movie> getRecommendations(
            Mood mood,
            List<Integer> dislikedGenreIds
    );
}
