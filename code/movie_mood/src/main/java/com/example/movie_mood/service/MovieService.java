package com.example.movie_mood.service;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.Video;
import com.example.movie_mood.domain.model.MoviePage;

import java.util.List;

public interface MovieService {

    MoviePage browseMovies(int page);

    MoviePage searchMovies(String keyword, int page);

    List<Movie> filterMoviesByMood(Mood mood);

    List<Movie> getMovieBatch(List<String> tmdbMovieIds);

    Movie getMovieDetails(String tmdbMovieId);

    Movie getMovieSummaryForHistory(String tmdbMovieId);

    Video getMovieTrailer(String tmdbMovieId);

    List<Movie> filterMoviesByGenre(Integer genreId);

    MoviePage discoverMovies(
            List<Integer> genreIds,
            Integer startYear,
            Integer endYear,
            Double minRating,
            String sortBy,
            int page);
}
