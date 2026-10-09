package com.example.movie_mood.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String tmdbMovieId) {
        super("Movie not found with TMDB ID: " + tmdbMovieId);
    }
}