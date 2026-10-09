package com.example.movie_mood.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaylistMovieBatchRequest {
    @NotBlank(message = "tmdbMovieId is required")
    private String tmdbMovieId;

    private List<UUID> addToPlaylistIds = new ArrayList<>();
    private List<UUID> removeFromPlaylistIds = new ArrayList<>();

    public PlaylistMovieBatchRequest() {
    }

    public String getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(String tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }

    public List<UUID> getAddToPlaylistIds() {
        return addToPlaylistIds;
    }

    public void setAddToPlaylistIds(List<UUID> addToPlaylistIds) {
        this.addToPlaylistIds = addToPlaylistIds == null ? new ArrayList<>() : addToPlaylistIds;
    }

    public List<UUID> getRemoveFromPlaylistIds() {
        return removeFromPlaylistIds;
    }

    public void setRemoveFromPlaylistIds(List<UUID> removeFromPlaylistIds) {
        this.removeFromPlaylistIds = removeFromPlaylistIds == null ? new ArrayList<>() : removeFromPlaylistIds;
    }
}
