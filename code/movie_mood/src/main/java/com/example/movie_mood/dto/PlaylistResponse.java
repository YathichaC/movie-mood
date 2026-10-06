package com.example.movie_mood.dto;

import com.example.movie_mood.domain.entity.Playlist;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlaylistResponse {
    private UUID playlistId;
    private UUID userId;
    private String playlistName;
    private String coverImagePath;
    private int itemCount;
    private List<MovielistResponse> items;

    public PlaylistResponse() {}

    public PlaylistResponse(Playlist playlist) {
        this.playlistId = playlist.getPlaylistId();
        this.userId = playlist.getUserId();
        this.playlistName = playlist.getPlaylistName();
        this.coverImagePath = playlist.getCoverImagePath();

        if (playlist.getItems() != null) {
            this.itemCount = playlist.getItems().size();
            this.items = playlist.getItems().stream()
                    .map(MovielistResponse::new)
                    .collect(Collectors.toList());
        }
    }

    public UUID getPlaylistId() {
        return playlistId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public int getItemCount() {
        return itemCount;
    }

    public List<MovielistResponse> getItems() {
        return items;
    }
}