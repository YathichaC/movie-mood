package com.example.movie_mood.dto.response;

import java.time.LocalDate;
import java.util.List;

public class MovieResponse {

    private String tmdbMovieId;
    private String title;
    private String synopsis;
    private Double rating;
    private LocalDate releaseDate;
    private List<String> genreIds;
    private List<String> genres;
    private String posterPath;
    private String backdropPath;

    public MovieResponse() {
    }

    public MovieResponse(
            String tmdbMovieId,
            String title,
            String synopsis,
            Double rating,
            LocalDate releaseDate,
            List<String> genreIds) {

        this.tmdbMovieId = tmdbMovieId;
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
    }

    public String getTmdbMovieId() {
        return tmdbMovieId;
    }

    public void setTmdbMovieId(String tmdbMovieId) {
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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
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

    public List<String> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}