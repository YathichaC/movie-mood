
package com.example.movie_mood.domain.entity;

import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "WatchHistory", uniqueConstraints = {
        @UniqueConstraint(name = "uk_watch_history_user_movie", columnNames = { "user_id", "tmdb_movie_id" })
})
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", nullable = false, updatable = false)
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public void setTmdbMovieId(String tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }
}