package com.example.movie_mood.dto;

import java.util.UUID;

public class PlaylistPickerResponse {
    private UUID playlistId;
    private String playlistName;
    private int itemCount;
    private boolean containsCurrentMovie;

    public PlaylistPickerResponse() {
    }

    public PlaylistPickerResponse(UUID playlistId, String playlistName, int itemCount) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.itemCount = itemCount;
    }

    public PlaylistPickerResponse(UUID playlistId, String playlistName, int itemCount, boolean containsCurrentMovie) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.itemCount = itemCount;
        this.containsCurrentMovie = containsCurrentMovie;
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

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean isContainsCurrentMovie() {
        return containsCurrentMovie;
    }

    public void setContainsCurrentMovie(boolean containsCurrentMovie) {
        this.containsCurrentMovie = containsCurrentMovie;
    }

    public boolean isContainsMovie() {
        return containsCurrentMovie;
    }

    public void setContainsMovie(boolean containsMovie) {
        this.containsCurrentMovie = containsMovie;
    }

    public boolean isInPlaylist() {
        return containsCurrentMovie;
    }

    public void setInPlaylist(boolean inPlaylist) {
        this.containsCurrentMovie = inPlaylist;
    }
}
