package com.example.movie_mood.exception;

public class GenreAlreadyExistsException extends RuntimeException {

    public GenreAlreadyExistsException(String genreId) {
        super("Genre already exists with id: " + genreId);
    }
}