package com.example.movie_mood.dto;

import java.time.LocalDate;

public class WatchHistoryMovieResponse {
    private String id;
    private String title;
    private String posterPath;
    private LocalDate releaseDate;
    private Double rating;

    public WatchHistoryMovieResponse() {
    }

    public WatchHistoryMovieResponse(
            String id,
            String title,
            String posterPath,
            LocalDate releaseDate,
            Double rating) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
