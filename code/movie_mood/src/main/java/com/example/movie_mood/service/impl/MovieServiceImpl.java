package com.example.movie_mood.service.impl;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.integration.tmdb.MovieProvider;
import com.example.movie_mood.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieProvider movieProvider;

    public MovieServiceImpl(MovieProvider movieProvider) {
        this.movieProvider = movieProvider;
    }

    @Override
    public List<Movie> browseMovies() {
        return movieProvider.getPopularMovies();
    }

    @Override
    public List<Movie> searchMovies(String keyword) {
        return movieProvider.searchMovies(keyword);
    }

    @Override
    public Movie getMovieDetails(Long tmdbMovieId) {
        return movieProvider.getMovie(tmdbMovieId);
    }

    @Override
    public List<Movie> filterMoviesByGenre(Integer genreId) {
    return movieProvider.discoverMoviesByGenres(
            List.of(genreId)
    );
}
}