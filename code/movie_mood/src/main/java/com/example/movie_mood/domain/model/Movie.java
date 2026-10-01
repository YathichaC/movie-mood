package com.example.movie_mood.domain.model;

import java.time.LocalDate;
import java.util.List;

public class Movie {

    private Long tmdbMovieId;
    private String title;
    private String synopsis;
    private Double rating;
    private LocalDate releaseDate;
    private List<Integer> genreIds;

    public Movie() {
    }

    public Movie(
            Long tmdbMovieId,
            String title,
            String synopsis,
            Double rating,
            LocalDate releaseDate,
            List<Integer> genreIds) {

        this.tmdbMovieId = tmdbMovieId;
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
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

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }
}