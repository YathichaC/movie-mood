package com.example.movie_mood.dto;
import java.util.UUID;

public class WatchHistoryResponse {
    private UUID historyId;
    private String tmdbMovieId;

    public WatchHistoryResponse() {}

    public WatchHistoryResponse(UUID historyId, String tmdbMovieId) {
        this.historyId = historyId;
        this.tmdbMovieId = tmdbMovieId;
    }

    public UUID getHistoryId() { return historyId; }
    public void setHistoryId(UUID historyId) { this.historyId = historyId; }

    public String getTmdbMovieId() { return tmdbMovieId; }
    public void setTmdbMovieId(String tmdbMovieId) { this.tmdbMovieId = tmdbMovieId; }
}