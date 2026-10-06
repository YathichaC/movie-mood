package com.example.movie_mood.dto;

import jakarta.validation.constraints.NotBlank;

public class WatchHistoryRequest {
    @NotBlank(message = "tmdbMovieId is required")
    private String tmdbMovieId;

    public WatchHistoryRequest() {}

    public WatchHistoryRequest(String tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }

    public String getTmdbMovieId() { return tmdbMovieId; }
    public void setTmdbMovieId(String tmdbMovieId) { this.tmdbMovieId = tmdbMovieId; }
}