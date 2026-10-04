package com.example.movie_mood.strategy;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.mapper.MoodGenreMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("defaultMatchScoreStrategy")
public class DefaultMatchScoreStrategy implements MatchScoreStrategy {

    private final MoodGenreMapper moodGenreMapper;

    public DefaultMatchScoreStrategy(MoodGenreMapper moodGenreMapper) {
        this.moodGenreMapper = moodGenreMapper;
    }

    @Override
    public double calculateScore(Movie movie, Mood mood) {
        double score = 40.0;

        if (movie.getRating() != null) {
            score += Math.min(movie.getRating() * 4.0, 40.0);
        }

        List<Integer> targetGenres = mood != null ? moodGenreMapper.getGenreIds(mood) : List.of();
        if (movie.getGenreIds() != null && !Collections.disjoint(movie.getGenreIds(), targetGenres)) {
            score += 25.0;
        }

        return Math.min(score, 100.0);
    }
}