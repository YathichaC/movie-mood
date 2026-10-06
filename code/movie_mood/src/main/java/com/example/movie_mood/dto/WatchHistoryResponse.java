package com.example.movie_mood.dto;
import java.util.UUID;
import java.time.LocalDateTime;

public class WatchHistoryResponse {
    private UUID history_id;
    private String tmdbMovieId;

    public WatchHistoryResponse() {}

    public WatchHistoryResponse(UUID history_id, String tmdbMovieId) {
        this.history_id = history_id;
        this.tmdbMovieId = tmdbMovieId;
    }

    public UUID getHistoryId() { return history_id; }
    public void setHistoryId(UUID history_id) { this.history_id = history_id; }

    public String getTmdbMovieId() { return tmdbMovieId; }
    public void setTmdbMovieId(String tmdbMovieId) { this.tmdbMovieId = tmdbMovieId; }
}