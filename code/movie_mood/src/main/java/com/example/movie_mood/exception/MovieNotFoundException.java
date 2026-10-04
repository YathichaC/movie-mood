package com.example.movie_mood.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(Long tmdbMovieId) {
        super("Movie not found with TMDB ID: " + tmdbMovieId);
    }
}