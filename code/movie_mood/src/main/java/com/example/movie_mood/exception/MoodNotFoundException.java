package com.example.movie_mood.exception;

public class MoodNotFoundException extends RuntimeException {

    public MoodNotFoundException(String mood) {
        super("Mood not found: " + mood);
    }
}