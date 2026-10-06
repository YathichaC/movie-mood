package com.example.movie_mood.dto;

import jakarta.validation.constraints.NotBlank;

public class MovielistRequest {
    private String tmdbMovieId;
    
    @NotBlank(message = "tmdbMovieId is required")
    public MovielistRequest() {}

    public MovielistRequest(String tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }

    public String getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(String tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }
}