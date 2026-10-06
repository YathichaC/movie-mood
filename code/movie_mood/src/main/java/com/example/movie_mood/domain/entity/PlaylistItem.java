package com.example.movie_mood.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"playlist_id", "tmdb_movie_id"})
})
public class PlaylistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Column(name = "tmdb_movie_id", nullable = false)
    private Long tmdbMovieId;

    @Column(nullable = false)
    private String title;

    @Column(name = "poster_path")
    private String posterPath;

    private Double rating;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    public PlaylistItem() {
        this.addedAt = LocalDateTime.now();
    }

    public PlaylistItem(Playlist playlist, Long tmdbMovieId, String title, String posterPath, Double rating) {
        this.playlist = playlist;
        this.tmdbMovieId = tmdbMovieId;
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
        this.addedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public Long getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(Long tmdbMovieId) {
        this.tmdbMovieId = tmdbMovieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}