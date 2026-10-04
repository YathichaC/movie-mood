package com.example.movie_mood.strategy;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.mapper.MoodGenreMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("moodFocusedStrategy")
public class MoodFocusedStrategy implements MatchScoreStrategy {

    private final MoodGenreMapper moodGenreMapper;

    public MoodFocusedStrategy(MoodGenreMapper moodGenreMapper) {
        this.moodGenreMapper = moodGenreMapper;
    }

    @Override
    public double calculateScore(Movie movie, Mood mood) {
        double score = 30.0;

        if (movie.getRating() != null) {
            score += Math.min(movie.getRating() * 2.0, 20.0);
        }

        List<Integer> targetGenres = mood != null ? moodGenreMapper.getGenreIds(mood) : List.of();
        if (movie.getGenreIds() != null && !targetGenres.isEmpty()) {
            long matchCount = movie.getGenreIds().stream()
                    .filter(targetGenres::contains)
                    .count();
            score += Math.min(matchCount * 25.0, 50.0);
        }

        return Math.min(score, 100.0);
    }
}