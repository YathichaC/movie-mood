package com.example.movie_mood.domain.entity;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "Movielist")
public class Movielist {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Column(name = "tmdb_movie_id")
    private String tmdbMovieId;

    public Movielist() {
    }

    public Movielist(Playlist playlist, String tmdbMovieId) {
        this.playlist = playlist;
        this.tmdbMovieId = tmdbMovieId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public String getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(String tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }
}