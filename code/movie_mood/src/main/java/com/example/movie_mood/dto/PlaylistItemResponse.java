package com.example.movie_mood.dto;

import com.example.movie_mood.domain.entity.PlaylistItem;
import java.time.LocalDateTime;

public class PlaylistItemResponse {
    private Long id;
    private Long tmdbMovieId;
    private String title;
    private String posterPath;
    private Double rating;
    private LocalDateTime addedAt;

    public PlaylistItemResponse() {}

    public PlaylistItemResponse(PlaylistItem item) {
        this.id = item.getId();
        this.tmdbMovieId = item.getTmdbMovieId();
        this.title = item.getTitle();
        this.posterPath = item.getPosterPath();
        this.rating = item.getRating();
        this.addedAt = item.getAddedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getTmdbMovieId() {
        return tmdbMovieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Double getRating() {
        return rating;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }
}