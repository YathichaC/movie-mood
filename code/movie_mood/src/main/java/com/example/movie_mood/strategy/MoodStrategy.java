package com.example.movie_mood.strategy;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.MovieService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component("moodStrategy")
public class MoodStrategy implements Strategy {

    private final MovieService movieService;
    private final MatchScoreStrategy defaultStrategy;

    public MoodStrategy(
            MovieService movieService,
            @Qualifier("defaultMatchScoreStrategy") MatchScoreStrategy defaultStrategy) {
        this.movieService = movieService;
        this.defaultStrategy = defaultStrategy;
    }

    @Override
    public List<Movie> recommend(Mood mood, List<Integer> dislikedGenreIds) {
        return recommend(mood, dislikedGenreIds, defaultStrategy);
    }

    public List<Movie> recommend(Mood mood, List<Integer> dislikedGenreIds, MatchScoreStrategy strategy) {
        MatchScoreStrategy scoreStrategy = strategy != null ? strategy : defaultStrategy;

        List<Movie> allMovies = movieService.browseMovies();
        if (allMovies == null || allMovies.isEmpty()) {
            allMovies = getFallbackMockMovies();
        }

        List<Movie> recommended = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (dislikedGenreIds != null && isDisliked(movie, dislikedGenreIds)) {
                continue;
            }

            double score = scoreStrategy.calculateScore(movie, mood);
            movie.setMatchScore(score);

            recommended.add(movie);
        }

        recommended.sort(
            Comparator.comparing(Movie::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Movie::getRating, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Movie::getReleaseDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Movie::getTitle, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        return recommended.stream().limit(10).toList();
    }

    private boolean isDisliked(Movie movie, List<Integer> dislikedGenreIds) {
        if (movie.getGenreIds() == null) return false;
        return movie.getGenreIds().stream().anyMatch(dislikedGenreIds::contains);
    }

    private List<Movie> getFallbackMockMovies() {
        List<Movie> list = new ArrayList<>();

        Movie m1 = new Movie();
        m1.setTitle("Inside Out 2");
        m1.setRating(7.8);
        m1.setGenreIds(List.of(16, 35));
        list.add(m1);

        Movie m2 = new Movie();
        m2.setTitle("About Time");
        m2.setRating(8.2);
        m2.setGenreIds(List.of(10749, 18));
        list.add(m2);

        Movie m3 = new Movie();
        m3.setTitle("A Quiet Place: Day One");
        m3.setRating(6.5);
        m3.setGenreIds(List.of(27, 53));
        list.add(m3);

        return list;
    }
}