package com.example.movie_mood.dto;

public class PlaylistItemRequest {
    private Long tmdbMovieId;
    private String title;
    private String posterPath;
    private Double rating;

    public PlaylistItemRequest() {}

    public PlaylistItemRequest(Long tmdbMovieId, String title, String posterPath, Double rating) {
        this.tmdbMovieId = tmdbMovieId;
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
    }

    public Long getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(Long tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}