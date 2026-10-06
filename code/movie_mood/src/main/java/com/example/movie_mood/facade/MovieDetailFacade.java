package com.example.movie_mood.facade;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.service.MovieService;
import org.springframework.stereotype.Component;

@Component
public class MovieDetailFacade {

    private final MovieService movieService;

    public MovieDetailFacade(MovieService movieService) {
        this.movieService = movieService;
    }

    public Movie getMovieDetails(String tmdbMovieId) {
        return movieService.getMovieDetails(tmdbMovieId);
    }
}