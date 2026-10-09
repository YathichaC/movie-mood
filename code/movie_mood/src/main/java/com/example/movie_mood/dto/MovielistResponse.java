package com.example.movie_mood.dto;

import com.example.movie_mood.domain.entity.Movielist;
import java.util.UUID;

public class MovielistResponse {
    private UUID id;
    private String tmdbMovieId;

    public MovielistResponse() {}

    public MovielistResponse(Movielist item) {
        this.id = item.getId();
        this.tmdbMovieId = item.getTmdbMovieId();
    }

    public UUID getId() {
        return id;
    }

    public String getTmdbMovieId() {
        return tmdbMovieId;
    }
}