package com.example.movie_mood.dto;

public class WatchHistoryRequest {
    private Long tmdbMovieId;
    private String title;
    private String posterPath;

    public WatchHistoryRequest() {}

    public WatchHistoryRequest(Long tmdbMovieId, String title, String posterPath) {
        this.tmdbMovieId = tmdbMovieId;
        this.title = title;
        this.posterPath = posterPath;
    }

    public Long getTmdbMovieId() { return tmdbMovieId; }
    public void setTmdbMovieId(Long tmdbMovieId) { this.tmdbMovieId = tmdbMovieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
}