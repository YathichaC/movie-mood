package com.example.movie_mood.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Genre")
public class Genre {

    @Id
    @Column(name = "genre_id")
    private String genreId;

    @Column(name = "genre_name")
    private String genreName;

    public Genre() {
    }

    public Genre(String genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}