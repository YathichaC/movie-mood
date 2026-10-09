package com.example.movie_mood.dto;

import jakarta.validation.constraints.NotBlank;

public class GenreRequest {

    @NotBlank(message = "genreId is required")
    private String genreId;

    @NotBlank(message = "genreName is required")
    private String genreName;

    public GenreRequest() {
    }

    public GenreRequest(String genreId, String genreName) {
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