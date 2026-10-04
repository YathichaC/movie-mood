package com.example.movie_mood.service;


import com.example.movie_mood.domain.model.Movie;
import java.util.List;

public interface MovieService {

    List<Movie> browseMovies();

    List<Movie> searchMovies(String keyword);

    Movie getMovieDetails(Long tmdbMovieId);

    List<Movie> filterMoviesByGenre(Integer genreId);
}
