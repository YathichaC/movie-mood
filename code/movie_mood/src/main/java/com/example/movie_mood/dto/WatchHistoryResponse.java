package com.example.movie_mood.dto;

import java.time.LocalDateTime;

public class WatchHistoryResponse {
    private Long id;
    private Long tmdbMovieId;
    private String title;
    private String posterPath;
    private LocalDateTime watchedAt;

    public WatchHistoryResponse() {}

    public WatchHistoryResponse(Long id, Long tmdbMovieId, String title, String posterPath, LocalDateTime watchedAt) {
        this.id = id;
        this.tmdbMovieId = tmdbMovieId;
        this.title = title;
        this.posterPath = posterPath;
        this.watchedAt = watchedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTmdbMovieId() { return tmdbMovieId; }
    public void setTmdbMovieId(Long tmdbMovieId) { this.tmdbMovieId = tmdbMovieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }
}