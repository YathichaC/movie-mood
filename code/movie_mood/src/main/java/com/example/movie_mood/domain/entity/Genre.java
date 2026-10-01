package com.example.movie_mood.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @Column(name = "genre_id")
    private Integer genreId;

    @Column(name = "genre_name", nullable = false)
    private String genreName;

    public Genre() {
    }

    public Genre(Integer genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}