package com.example.movie_mood.dto;

import java.util.UUID;

public class PlaylistSummaryResponse {
    private UUID playlistId;
    private String playlistName;
    private String coverImagePath;
    private int itemCount;

    public PlaylistSummaryResponse() {
    }

    public PlaylistSummaryResponse(UUID playlistId, String playlistName, String coverImagePath, int itemCount) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.coverImagePath = coverImagePath;
        this.itemCount = itemCount;
    }

    public UUID getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(UUID playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
