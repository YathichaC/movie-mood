package com.example.movie_mood.dto;

import com.example.movie_mood.domain.entity.Playlist;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private int itemCount;
    private List<PlaylistItemResponse> items;

    public PlaylistResponse() {}

    public PlaylistResponse(Playlist playlist) {
        this.id = playlist.getId();
        this.name = playlist.getName();
        this.description = playlist.getDescription();
        this.createdAt = playlist.getCreatedAt();
        if (playlist.getItems() != null) {
            this.itemCount = playlist.getItems().size();
            this.items = playlist.getItems().stream()
                    .map(PlaylistItemResponse::new)
                    .collect(Collectors.toList());
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getItemCount() {
        return itemCount;
    }

    public List<PlaylistItemResponse> getItems() {
        return items;
    }
}