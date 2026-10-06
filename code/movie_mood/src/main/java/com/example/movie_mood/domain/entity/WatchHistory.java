package com.example.movie_mood.domain.entity;

import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "WatchHistory", uniqueConstraints = {
        @UniqueConstraint(name = "uk_watch_history_user_movie", columnNames = { "user_id", "tmdb_movie_id" })
})
public class WatchHistory {
    @Id
    @Column(name = "history_id")
    private UUID historyId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "tmdb_movie_id", nullable = false)
    private String tmdbMovieId;

    public WatchHistory() {
    }

    public WatchHistory(UUID userId, String tmdbMovieId) {
        this.userId = userId;
        this.tmdbMovieId = tmdbMovieId;
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID history_id) {
        this.historyId = history_id;
    }

    public void setId(UUID history_id) {
        this.historyId = history_id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(String tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }
}