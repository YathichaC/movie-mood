package com.example.movie_mood.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Playlist")
public class Playlist {

    @Id
    @Column(name = "playlist_id")
    private UUID playlistId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "playlist_name", nullable = false)
    private String playlistName;

    @OneToOne(
        mappedBy = "playlist",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )

    private PlaylistDetail detail;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movielist> items = new ArrayList<>();

    public Playlist() {
    }

    public Playlist(UUID userId, String playlistName) {
        this.userId = userId;
        this.playlistName = playlistName;
    }

    public void addItem(Movielist item) {
        items.add(item);
        item.setPlaylist(this);
    }

    public void removeItem(Movielist item) {
        items.remove(item);
        item.setPlaylist(null);
    }

    public UUID getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(UUID playlistId) {
        this.playlistId = playlistId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public PlaylistDetail getDetail() {
        return detail;
    }

    public void setDetail(PlaylistDetail detail) {
        this.detail = detail;
    }

    public List<Movielist> getItems() {
        return items;
    }

    public void setItems(List<Movielist> items) {
        this.items = items;
    }
}