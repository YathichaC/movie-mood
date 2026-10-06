package com.example.movie_mood.exception;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(Object genreId) {
        super("Genre not found with id: " + genreId);
    }
}