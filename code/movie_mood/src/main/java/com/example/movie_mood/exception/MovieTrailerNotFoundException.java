package com.example.movie_mood.exception;

public class MovieTrailerNotFoundException extends RuntimeException {

    public MovieTrailerNotFoundException(String tmdbMovieId) {
        super("Trailer not found for movie: " + tmdbMovieId);
    }
}
