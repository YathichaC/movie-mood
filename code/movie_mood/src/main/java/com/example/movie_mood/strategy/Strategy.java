package com.example.movie_mood.strategy;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import java.util.List;

public interface Strategy {
    List<Movie> recommend(Mood mood, List<Integer> dislikedGenreIds);
}