package com.example.movie_mood.domain.entity;

import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "Movielist", uniqueConstraints = {
        @UniqueConstraint(name = "movielist_playlist_movie_unique", columnNames = { "playlist_id", "tmdb_movie_id" })
})
public class Movielist {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
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