package com.example.movie_mood.integration.tmdb;

import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.MoviePage;
import com.example.movie_mood.domain.model.Video;

import java.util.List;

public interface MovieProvider {

    MoviePage getPopularMovies(int page);

    MoviePage searchMovies(String keyword, int page);

    Movie getMovie(String tmdbMovieId);

    List<Video> getMovieVideos(String tmdbMovieId);

    List<Movie> discoverMoviesByGenres(List<Integer> genreIds);

    MoviePage discoverMovies(
            List<Integer> genreIds,
            Integer startYear,
            Integer endYear,
            Double minRating,
            String sortBy,
            int page);
}