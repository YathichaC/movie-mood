package com.example.movie_mood.exception;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(Integer genreId) {
        super("Genre not found with id: " + genreId);
    }
}