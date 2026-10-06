package com.example.movie_mood.domain.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "PlaylistDetail")
public class PlaylistDetail {

    @Id
    @Column(name = "playlist_id")
    private UUID playlistId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @Column(name = "detail")
    private String detail;

    @Column(name = "cover_image_path")
    private String coverImagePath;

    public PlaylistDetail() {
    }

    public PlaylistDetail(Playlist playlist, String detail, String coverImagePath) {
        this.playlist = playlist;
        this.detail = detail;
        this.coverImagePath = coverImagePath;
    }

    public UUID getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(UUID playlistId) {
        this.playlistId = playlistId;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
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