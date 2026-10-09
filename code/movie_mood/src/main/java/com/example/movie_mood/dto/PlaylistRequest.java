package com.example.movie_mood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlaylistRequest {
    @NotBlank(message = "Playlist name is required")
    @Size(max = 100, message = "Playlist name must not exceed 100 characters")
    private String playlistName;
    private String detail;
    private String coverImagePath;

    public PlaylistRequest() {
    }

    public PlaylistRequest(String playlistName, String detail, String coverImagePath) {
        this.playlistName = playlistName;
        this.detail = detail;
        this.coverImagePath = coverImagePath;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }
}