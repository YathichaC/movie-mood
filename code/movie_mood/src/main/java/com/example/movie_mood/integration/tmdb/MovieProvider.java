package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.domain.model.Movie;
import java.util.List;

public interface MovieProvider {

    List<Movie> getPopularMovies();

    List<Movie> searchMovies(String keyword);

    Movie getMovie(Long tmdbMovieId);
}