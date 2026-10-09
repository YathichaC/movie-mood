package com.example.movie_mood.dto;
import java.util.UUID;

public class WatchHistoryResponse {
    private UUID historyId;
    private String tmdbMovieId;
    private boolean watched;

    public WatchHistoryResponse() {}

    public WatchHistoryResponse(UUID historyId, String tmdbMovieId) {
        this(historyId, tmdbMovieId, true);
    }

    public WatchHistoryResponse(UUID historyId, String tmdbMovieId, boolean watched) {
        this.historyId = historyId;
        this.tmdbMovieId = tmdbMovieId;
        this.watched = watched;
    }

    public UUID getHistoryId() { return historyId; }
    public void setHistoryId(UUID historyId) { this.historyId = historyId; }

    public String getTmdbMovieId() { return tmdbMovieId; }
    public void setTmdbMovieId(String tmdbMovieId) { this.tmdbMovieId = tmdbMovieId; }

    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }
}