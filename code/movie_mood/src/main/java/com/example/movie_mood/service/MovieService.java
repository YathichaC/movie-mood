package com.example.movie_mood.service;

import com.example.movie_mood.domain.enums.Mood;
import com.example.movie_mood.domain.model.Movie;
import com.example.movie_mood.domain.model.Video;
import com.example.movie_mood.domain.model.MoviePage;

import java.util.List;

public interface MovieService {

    List<Movie> browseMovies();

    MoviePage searchMovies(String keyword, int page);

    List<Movie> filterMoviesByMood(Mood mood);
    
    Movie getMovieDetails(String tmdbMovieId);

    Video getMovieTrailer(String tmdbMovieId);

    List<Movie> filterMoviesByGenre(Integer genreId);
}
