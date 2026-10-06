package com.example.movie_mood.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watch_histories")
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tmdb_movie_id", nullable = false)
    private Long tmdbMovieId;

    @Column(nullable = false)
    private String title;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "watched_at", nullable = false)
    private LocalDateTime watchedAt;

    public WatchHistory() {}

    public WatchHistory(Long userId, Long tmdbMovieId, String title, String posterPath) {
        this.userId = userId;
        this.tmdbMovieId = tmdbMovieId;
        this.title = title;
        this.posterPath = posterPath;
        this.watchedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTmdbMovieId() { return tmdbMovieId; }
    public void setTmdbMovieId(Long tmdbMovieId) { this.tmdbMovieId = tmdbMovieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }
}