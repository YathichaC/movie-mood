package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.domain.model.Movie;
import java.util.List;

public interface MovieProvider {

    List<Movie> getPopularMovies();

    List<Movie> searchMovies(String keyword);

    Movie getMovie(String tmdbMovieId);

    List<Video> getMovieVideos(String tmdbMovieId);

    List<Movie> discoverMoviesByGenres(List<Integer> genreIds);
}